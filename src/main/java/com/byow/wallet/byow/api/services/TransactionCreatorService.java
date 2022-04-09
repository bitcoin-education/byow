package com.byow.wallet.byow.api.services;

import com.byow.wallet.byow.domains.Utxo;
import com.byow.wallet.byow.utils.Satoshi;
import io.github.bitcoineducation.bitcoinjava.*;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.byow.wallet.byow.utils.Fee.totalCalculatedFee;
import static io.github.bitcoineducation.bitcoinjava.AddressConstants.*;

@Service
public class TransactionCreatorService {
    private static final String DUMMY_SIGNATURE = "0".repeat(144);
    private static final String DUMMY_PUBKEY = "0".repeat(66);

    private final DustCalculator dustCalculator;

    public TransactionCreatorService(DustCalculator dustCalculator) {
        this.dustCalculator = dustCalculator;
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

        if (dustCalculator.isDust(change)) {
            return transaction;
        }

        transactionOutputs.add(buildOutput(changeAddress, BigInteger.ONE));
        totalFee = totalCalculatedFee(transaction, feeRate);
        change = inputSum.subtract(amountToSend).subtract(totalFee);

        transactionOutputs.remove(1);
        if (dustCalculator.isDust(change)) {
            return transaction;
        }
        transactionOutputs.add(buildOutput(changeAddress, change));

        return transaction;
    }

    private TransactionOutput buildOutput(String address, BigInteger amount) {
        String prefix = parsePrefix(address);
        Script script = Script.p2wpkhScript(Bech32.decode(prefix, address)[1]);
        return new TransactionOutput(amount, script);
    }

    private String parsePrefix(String address) {
        return Stream.of(
            REGTEST_P2WPKH_ADDRESS_PREFIX,
            TESTNET_P2WPKH_ADDRESS_PREFIX,
            MAINNET_P2WPKH_ADDRESS_PREFIX
        )
        .filter(address::startsWith)
        .findFirst()
        .orElse("");
    }

    private ArrayList<TransactionInput> buildInputs(List<Utxo> selectedUtxos) {
        return selectedUtxos.stream()
            .map(utxo -> new TransactionInput(
                utxo.txid(),
                BigInteger.valueOf(utxo.vout()),
                new Script(List.of()),
                new BigInteger(1, Hex.decode("ffffffff"))
            )).peek(transactionInput -> {
                transactionInput.setWitness(new Witness(List.of(DUMMY_SIGNATURE, DUMMY_PUBKEY)));
            }).collect(Collectors.toCollection(ArrayList::new));
    }
}
