package com.byow.wallet.byow.api.services;

import io.github.bitcoineducation.bitcoinjava.AddressConstants;
import io.github.bitcoineducation.bitcoinjava.ExtendedKey;
import org.springframework.stereotype.Service;

@Service
public class SegwitAddressGenerator implements AddressGenerator {
    @Override
    public String generate(ExtendedKey extendedKey) {
        return extendedKey.toPublicKey().segwitAddressFromCompressedPublicKey(AddressConstants.MAINNET_P2WPKH_ADDRESS_PREFIX);
    }
}
