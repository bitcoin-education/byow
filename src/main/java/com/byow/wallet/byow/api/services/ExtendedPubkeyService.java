package com.byow.wallet.byow.api.services;

import com.byow.wallet.byow.domains.AddressType;
import com.byow.wallet.byow.domains.ExtendedPubkey;
import io.github.bitcoineducation.bitcoinjava.ExtendedKeyPrefix;
import io.github.bitcoineducation.bitcoinjava.ExtendedPrivateKey;
import org.springframework.stereotype.Service;

@Service
public class ExtendedPubkeyService {
    public ExtendedPubkey create(ExtendedPrivateKey masterKey, String derivationPath, AddressType addressType, ExtendedKeyPrefix extendedKeyPrefix) {
        return new ExtendedPubkey(masterKey.ckd(derivationPath, false, extendedKeyPrefix.getPublicPrefix()).serialize(), addressType.toString());
    }
}
