package com.byow.wallet.byow.api.services;

import io.github.bitcoineducation.bitcoinjava.ExtendedKey;
import io.github.bitcoineducation.bitcoinjava.Hash160;
import io.github.bitcoineducation.bitcoinjava.Script;
import org.springframework.stereotype.Service;

@Service
public class NestedSegwitAddressGenerator implements AddressGenerator {
    @Override
    public String generate(ExtendedKey extendedKey, String prefix) {
        return Script.p2wpkhScript(Hash160.hashToHex(extendedKey.toPublicKey().getCompressedPublicKey())).p2shAddress(prefix); //TODO: mudar para lib
    }
}
