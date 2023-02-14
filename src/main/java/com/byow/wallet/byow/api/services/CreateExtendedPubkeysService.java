package com.byow.wallet.byow.api.services;

import com.byow.wallet.byow.domains.AddressConfig;
import com.byow.wallet.byow.domains.ExtendedPubkey;
import io.github.bitcoineducation.bitcoinjava.ExtendedPrivateKey;
import io.github.bitcoineducation.bitcoinjava.MnemonicSeed;
import org.springframework.stereotype.Service;

import java.util.List;

import static io.github.bitcoineducation.bitcoinjava.ExtendedKeyPrefixes.MAINNET_PREFIX;

@Service
public class CreateExtendedPubkeysService {
    private final List<AddressConfig> addressConfigs;

    private final ExtendedPubkeyService extendedPubkeyService;

    public CreateExtendedPubkeysService(List<AddressConfig> addressConfigs, ExtendedPubkeyService extendedPubkeyService) {
        this.addressConfigs = addressConfigs;
        this.extendedPubkeyService = extendedPubkeyService;
    }

    public List<ExtendedPubkey> create(String mnemonicSeedString, String password) {
        MnemonicSeed mnemonicSeed = new MnemonicSeed(mnemonicSeedString);
        ExtendedPrivateKey masterKey = mnemonicSeed.toMasterKey(password, MAINNET_PREFIX.getPrivatePrefix());
        return addressConfigs.stream()
            .flatMap(addressConfig ->
                addressConfig.derivationPaths()
                    .entrySet()
                    .stream()
                    .map(entry -> extendedPubkeyService.create(masterKey, entry.getValue(), entry.getKey(), addressConfig.extendedKeyPrefix()))
            ).toList();
    }
}
