package com.byow.wallet.byow.api.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigInteger;

@Service
public class DustCalculator {
    private final long dustRelayFee;

    private final AddressConfigFinder addressConfigFinder;

    public DustCalculator(
        @Value("${bitcoin.dustRelayFee}") long dustRelayFee,
        AddressConfigFinder addressConfigFinder
    ) {
        this.dustRelayFee = dustRelayFee;
        this.addressConfigFinder = addressConfigFinder;
    }

    public boolean isDust(BigInteger amountInSatoshis, String address) {
        int inputPlusOutputSize = addressConfigFinder.findByAddress(address)
            .orElseThrow()
            .inputPlusOutputSize();
        return amountInSatoshis.longValue() < inputPlusOutputSize * dustRelayFee / 1000;
    }
}
