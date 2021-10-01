package com.byow.wallet.byow.api.services;

import com.byow.wallet.byow.domains.AddressConfig;
import com.byow.wallet.byow.domains.ExtendedPubkey;
import io.github.bitcoineducation.bitcoinjava.ExtendedKey;
import io.github.bitcoineducation.bitcoinjava.ExtendedPrivateKey;
import io.github.bitcoineducation.bitcoinjava.MnemonicSeed;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExtendedPubkeyService {
    private final List<AddressConfig> addressConfigs;

    public ExtendedPubkeyService(List<AddressConfig> addressConfigs) {
        this.addressConfigs = addressConfigs;
    }

    public List<ExtendedPubkey> create(String mnemonicSeedString, String password) {
        MnemonicSeed mnemonicSeed = new MnemonicSeed(mnemonicSeedString);
        ExtendedPrivateKey masterKey = mnemonicSeed.toMasterKey(password);
        return addressConfigs.stream()
            .map(addressConfig -> new ExtendedPubkey(deriveKey(addressConfig.derivationPath(), masterKey), addressConfig.addressType().toString()))
            .toList();
    }

    private String deriveKey(String derivationPath, ExtendedPrivateKey masterKey) {
        ExtendedKey extendedPubKey = masterKey.ckd(derivationPath, false);
        return extendedPubKey.serialize();
    }
}
