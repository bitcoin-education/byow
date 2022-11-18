package com.byow.wallet.byow.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;

public class Satoshi {
    public static BigInteger toSatoshis(BigDecimal amount) {
        return amount.multiply(BigDecimal.valueOf(100_000_000)).toBigInteger();
    }

    public static BigDecimal toBtc(BigInteger amount) {
        return new BigDecimal(amount).divide(BigDecimal.valueOf(100_000_000), 8, RoundingMode.UNNECESSARY);
    }

    public static BigInteger btcPerKbToSatoshiPerByte(BigDecimal feeRate) {
        BigInteger rate = feeRate.multiply(BigDecimal.valueOf(100_000_000))
            .divide(BigDecimal.valueOf(1024), new MathContext(2, RoundingMode.FLOOR))
            .toBigInteger();
        if (rate.longValue() < 1) {
            rate = BigInteger.ONE;
        }
        return rate;
    }
}
