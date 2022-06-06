package com.byow.wallet.byow.api.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.stream.Stream;

import static io.github.bitcoineducation.bitcoinjava.AddressConstants.*;

@Service
public class DustCalculator {
    private static final int SEGWIT_INPUT_PLUS_OUTPUT_SIZE = 98;

    private static final int NESTED_SEGWIT_INPUT_PLUS_OUTPUT_SIZE = 180;

    private final long dustRelayFee;

    public DustCalculator(
        @Value("${bitcoin.dustRelayFee}") long dustRelayFee
    ) {
        this.dustRelayFee = dustRelayFee;
    }

    public boolean isDust(BigInteger amountInSatoshis, String address) {
        if (isSegwit(address)) {
            return amountInSatoshis.longValue() < SEGWIT_INPUT_PLUS_OUTPUT_SIZE * dustRelayFee / 1000;
        }
        return amountInSatoshis.longValue() < NESTED_SEGWIT_INPUT_PLUS_OUTPUT_SIZE * dustRelayFee / 1000;
    }

    private boolean isSegwit(String address) {
        return Stream.of(
            REGTEST_P2WPKH_ADDRESS_PREFIX,
            TESTNET_P2WPKH_ADDRESS_PREFIX,
            MAINNET_P2WPKH_ADDRESS_PREFIX
        ).anyMatch(address::startsWith);
    }
}
