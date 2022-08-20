package com.byow.wallet.byow.api.services;

import com.byow.wallet.byow.domains.Utxo;
import io.github.bitcoineducation.bitcoinjava.Script;
import io.github.bitcoineducation.bitcoinjava.TransactionInput;
import io.github.bitcoineducation.bitcoinjava.Witness;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Service
public class NestedSegwitInputBuilder implements TransactionInputBuilder {
    private static final String DUMMY_SIGNATURE = "0".repeat(144);

    private static final String DUMMY_PUBKEY = "0".repeat(66);

    private static final String DUMMY_SCRIPT_SIG_NESTED_SEGWIT = "0".repeat(44);

    @Override
    public TransactionInput build(Utxo utxo) {
        TransactionInput transactionInput = new TransactionInput(
            utxo.txid(),
            BigInteger.valueOf(utxo.vout()),
            new Script(new ArrayList<>(List.of(DUMMY_SCRIPT_SIG_NESTED_SEGWIT))),
            new BigInteger(1, Hex.decode("ffffffff"))
        );
        transactionInput.setWitness(new Witness(List.of(DUMMY_SIGNATURE, DUMMY_PUBKEY)));
        return transactionInput;
    }
}
