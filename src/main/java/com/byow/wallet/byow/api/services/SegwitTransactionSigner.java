package com.byow.wallet.byow.api.services;

import com.byow.wallet.byow.utils.Satoshi;
import io.github.bitcoineducation.bitcoinjava.PrivateKey;
import io.github.bitcoineducation.bitcoinjava.Transaction;
import io.github.bitcoineducation.bitcoinjava.TransactionECDSASigner;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;

@Service
public class SegwitTransactionSigner implements TransactionSigner {
    @Override
    public void sign(Transaction transaction, PrivateKey privateKey, int index, BigDecimal amount) throws IOException {
        TransactionECDSASigner.sign(transaction, privateKey, index, Satoshi.toSatoshis(amount), true);
    }
}
