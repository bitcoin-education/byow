package com.byow.wallet.byow.api.services;

import com.byow.wallet.byow.domains.Utxo;
import com.byow.wallet.byow.utils.Satoshi;
import io.github.bitcoineducation.bitcoinjava.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.byow.wallet.byow.utils.Fee.totalCalculatedFee;

@Service
public class TransactionCreatorService {
    private final DustCalculator dustCalculator;

    private final List<ScriptPubkeyBuilder> scriptPubkeyBuilders;

    private final AddressConfigFinder addressConfigFinder;

    public TransactionCreatorService(DustCalculator dustCalculator, List<ScriptPubkeyBuilder> scriptPubkeyBuilders, AddressConfigFinder addressConfigFinder) {
        this.dustCalculator = dustCalculator;
        this.scriptPubkeyBuilders = scriptPubkeyBuilders;
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
        Script script = scriptPubkeyBuilders.stream()
            .filter(scriptPubkeyBuilder -> scriptPubkeyBuilder.match(address))
            .findFirst()
            .orElseThrow()
            .build(address);
        return new TransactionOutput(amount, script);
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
