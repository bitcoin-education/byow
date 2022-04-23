package com.byow.wallet.byow.utils;

import io.github.bitcoineducation.bitcoinjava.Transaction;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

public class Fee {
    public static BigInteger totalCalculatedFee(Transaction transaction, BigDecimal feeRate) {
        try {
            BigInteger feeRateInSatoshisPerVByte = Satoshi.btcPerKbToSatoshiPerByte(feeRate);
            return feeRateInSatoshisPerVByte.multiply(BigInteger.valueOf(transaction.getVSize()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}