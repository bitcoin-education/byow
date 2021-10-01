package com.byow.wallet.byow.api.services;

import io.github.bitcoineducation.bitcoinjava.ExtendedKey;
import org.springframework.stereotype.Component;

import static io.github.bitcoineducation.bitcoinjava.AddressConstants.MAINNET_P2WPKH_ADDRESS_PREFIX;

@Component
public class SegwitAddressGenerator implements AddressGenerator {
    @Override
    public String generate(ExtendedKey extendedKey) {
        return extendedKey.toPublicKey().segwitAddressFromCompressedPublicKey(MAINNET_P2WPKH_ADDRESS_PREFIX);
    }
}
