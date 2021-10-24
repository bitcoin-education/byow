package com.byow.wallet.byow.api.services;

import com.byow.wallet.byow.domains.AddressType;
import com.byow.wallet.byow.domains.ExtendedPubkey;
import io.github.bitcoineducation.bitcoinjava.ExtendedPrivateKey;
import org.springframework.stereotype.Service;

import java.util.Map;

import static io.github.bitcoineducation.bitcoinjava.ExtendedKeyPrefixes.MAINNET_SEGWIT_PREFIX;

@Service
public class ExtendedPubkeyService {
    private final static Map<AddressType, String> prefixMap = Map.of(
        AddressType.SEGWIT, MAINNET_SEGWIT_PREFIX.getPublicPrefix(),
        AddressType.SEGWIT_CHANGE, MAINNET_SEGWIT_PREFIX.getPublicPrefix()
    );

    public ExtendedPubkey create(ExtendedPrivateKey masterKey, String derivationPath, AddressType addressType) {
        return new ExtendedPubkey(masterKey.ckd(derivationPath, false, prefixMap.get(addressType)).serialize(), addressType.toString());
    }
}
