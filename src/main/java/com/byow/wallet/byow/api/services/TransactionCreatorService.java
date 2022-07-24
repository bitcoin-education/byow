package com.byow.wallet.byow.api.services;

import com.byow.wallet.byow.domains.Utxo;
import com.byow.wallet.byow.domains.node.ErrorMessages;
import com.byow.wallet.byow.gui.exceptions.CreateTransactionException;
import com.byow.wallet.byow.utils.Satoshi;
import io.github.bitcoineducation.bitcoinjava.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.byow.wallet.byow.utils.Fee.totalCalculatedFee;

@Service
public class TransactionCreatorService {

    private final static Logger logger = LoggerFactory.getLogger(TransactionCreatorService.class);

    private final DustCalculator dustCalculator;

    private final ScriptConfigFinder scriptConfigFinder;

    private final AddressConfigFinder addressConfigFinder;

    public TransactionCreatorService(
        DustCalculator dustCalculator,
        ScriptConfigFinder scriptConfigFinder,
        AddressConfigFinder addressConfigFinder
    ) {
        this.dustCalculator = dustCalculator;
        this.scriptConfigFinder = scriptConfigFinder;
        this.addressConfigFinder = addressConfigFinder;
    }

    public Transaction create(List<Utxo> utxos, String addressToSend, BigInteger amountToSend, String changeAddress, BigDecimal feeRate) {
        ArrayList<TransactionInput> transactionInputs = buildInputs(utxos);

        TransactionOutput addressToSendOutput = buildOutput(addressToSend, amountToSend);
        ArrayList<TransactionOutput> transactionOutputs = new ArrayList<>(List.of(addressToSendOutput));
        Transaction transaction = new Transaction(BigInteger.ONE, transactionInputs, transactionOutputs, BigInteger.ZERO, true);

        BigInteger totalFee = totalCalculatedFee(transaction, feeRate);
        BigInteger inputSum = utxos.stream()
            .map(Utxo::amount)
            .reduce(BigDecimal::add)
            .map(Satoshi::toSatoshis)
            .orElse(BigInteger.ZERO);
        BigInteger change = inputSum.subtract(amountToSend).subtract(totalFee);

        if (dustCalculator.isDust(change, changeAddress)) {
            return transaction;
        }

        transactionOutputs.add(buildOutput(changeAddress, BigInteger.ONE));
        totalFee = totalCalculatedFee(transaction, feeRate);
        change = inputSum.subtract(amountToSend).subtract(totalFee);

        transactionOutputs.remove(1);
        if (dustCalculator.isDust(change, changeAddress)) {
            return transaction;
        }
        transactionOutputs.add(buildOutput(changeAddress, change));

        return transaction;
    }

    private TransactionOutput buildOutput(String address, BigInteger amount) {
        try {
            Script script = scriptConfigFinder.findByAddress(address)
                .scriptPubkeyBuilder()
                .build(address);
            return new TransactionOutput(amount, script);
        } catch (Exception e) {
            logger.error("Error building output", e);
            throw new CreateTransactionException(ErrorMessages.INVALID_ADDRESS);
        }
    }

    private ArrayList<TransactionInput> buildInputs(List<Utxo> utxos) {
        return utxos.stream()
            .map(utxo -> addressConfigFinder.findByAddress(utxo.address())
                .orElseThrow()
                .transactionInputBuilder()
                .build(utxo)
            ).collect(Collectors.toCollection(ArrayList::new));
    }
}
