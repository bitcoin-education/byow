package com.byow.wallet.byow.api.services;

import io.github.bitcoineducation.bitcoinjava.ExtendedKey;
import io.github.bitcoineducation.bitcoinjava.Hash160;
import io.github.bitcoineducation.bitcoinjava.Script;
import org.springframework.stereotype.Service;

import static com.byow.wallet.byow.domains.AddressType.NESTED_SEGWIT;

@Service
public class NestedSegwitAddressGenerator implements AddressGenerator {
    private final AddressPrefixFactory addressPrefixFactory;

    public NestedSegwitAddressGenerator(AddressPrefixFactory addressPrefixFactory) {
        this.addressPrefixFactory = addressPrefixFactory;
    }

    @Override
    public String generate(ExtendedKey extendedKey) {
        return Script.p2wpkhScript(Hash160.hashToHex(extendedKey.toPublicKey().getCompressedPublicKey())).p2shAddress(addressPrefixFactory.get(NESTED_SEGWIT)); //TODO: mudar para lib
    }
}
