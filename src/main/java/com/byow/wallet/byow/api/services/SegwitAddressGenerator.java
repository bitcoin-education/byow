package com.byow.wallet.byow.api.services;

import io.github.bitcoineducation.bitcoinjava.ExtendedKey;
import org.springframework.stereotype.Component;

import static io.github.bitcoineducation.bitcoinjava.AddressConstants.TESTNET_P2WPKH_ADDRESS_PREFIX;

@Component
public class SegwitAddressGenerator implements AddressGenerator {
    @Override
    public String generate(ExtendedKey extendedKey) {
        return extendedKey.toPublicKey().segwitAddressFromCompressedPublicKey(TESTNET_P2WPKH_ADDRESS_PREFIX);
    }
}
