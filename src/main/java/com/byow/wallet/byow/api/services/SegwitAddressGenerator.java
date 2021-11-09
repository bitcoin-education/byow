package com.byow.wallet.byow.api.services;

import io.github.bitcoineducation.bitcoinjava.ExtendedKey;
import org.springframework.stereotype.Service;

import static com.byow.wallet.byow.domains.AddressType.SEGWIT;

@Service
public class SegwitAddressGenerator implements AddressGenerator {
    private final AddressPrefixFactory addressPrefixFactory;

    public SegwitAddressGenerator(AddressPrefixFactory addressPrefixFactory) {
        this.addressPrefixFactory = addressPrefixFactory;
    }

    @Override
    public String generate(ExtendedKey extendedKey) {
        return extendedKey.toPublicKey().segwitAddressFromCompressedPublicKey(addressPrefixFactory.get(SEGWIT));
    }
}
