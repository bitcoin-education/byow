package com.byow.wallet.byow.api.services;

import io.github.bitcoineducation.bitcoinjava.ExtendedKey;
import org.springframework.stereotype.Service;

@Service
public class SegwitAddressGenerator implements AddressGenerator {
    @Override
    public String generate(ExtendedKey extendedKey, String prefix) {
        return extendedKey.toPublicKey().segwitAddressFromCompressedPublicKey(prefix);
    }
}
