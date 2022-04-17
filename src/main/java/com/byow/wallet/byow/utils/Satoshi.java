package com.byow.wallet.byow.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

import static java.math.BigDecimal.valueOf;
import static java.math.RoundingMode.FLOOR;
import static java.math.RoundingMode.UNNECESSARY;

public class Satoshi {
    public static BigInteger toSatoshis(BigDecimal amount) {
        return amount.multiply(valueOf(100_000_000)).toBigInteger();
    }

    public static BigDecimal toBtc(BigInteger amount) {
        return new BigDecimal(amount).divide(valueOf(100_000_000), 8, UNNECESSARY);
    }

    public static BigInteger btcPerKbToSatoshiPerByte(BigDecimal feeRate) {
        BigInteger rate = feeRate.multiply(valueOf(100_000_000))
            .divide(valueOf(1024), new MathContext(2, FLOOR))
            .toBigInteger();
        if (rate.longValue() < 1) {
            rate = BigInteger.ONE;
        }
        return rate;
    }

}