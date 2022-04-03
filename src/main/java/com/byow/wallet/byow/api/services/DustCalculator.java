package com.byow.wallet.byow.api.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigInteger;

@Service
public class DustCalculator {
    private final long dustRelayFee;

    public DustCalculator(
        @Value("${bitcoin.dustRelayFee}") long dustRelayFee
    ) {
        this.dustRelayFee = dustRelayFee;
    }

    public boolean isDust(BigInteger amountInSatoshis) {
        return amountInSatoshis.longValue() < 98 * dustRelayFee / 1000;
    }
}
