package com.byow.wallet.byow.api.services;

import io.github.bitcoineducation.bitcoinjava.PrivateKey;
import io.github.bitcoineducation.bitcoinjava.Transaction;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

public interface TransactionSigner {
    void sign(Transaction transaction, PrivateKey privateKey, int index, BigDecimal amount) throws IOException;
}
