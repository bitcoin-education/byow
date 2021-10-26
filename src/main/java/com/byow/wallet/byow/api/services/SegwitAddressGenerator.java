package com.byow.wallet.byow.api.services;

import com.byow.wallet.byow.api.config.AddressConfiguration;
import io.github.bitcoineducation.bitcoinjava.ExtendedKey;
import org.springframework.stereotype.Service;

@Service
public class SegwitAddressGenerator implements AddressGenerator {
    private final AddressConfiguration addressConfiguration;

    public SegwitAddressGenerator(AddressConfiguration addressConfiguration) {
        this.addressConfiguration = addressConfiguration;
    }

    @Override
    public String generate(ExtendedKey extendedKey) {
        return extendedKey.toPublicKey().segwitAddressFromCompressedPublicKey(addressConfiguration.getP2WPKHAddressPrefix());
    }
}
