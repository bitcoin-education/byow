package com.byow.wallet.byow.api.services;

import com.byow.wallet.byow.domains.AddressConfig;
import com.byow.wallet.byow.domains.ExtendedPubkey;
import com.byow.wallet.byow.domains.Wallet;
import io.github.bitcoineducation.bitcoinjava.ExtendedPrivateKey;
import io.github.bitcoineducation.bitcoinjava.MnemonicSeed;
import org.springframework.stereotype.Component;

import java.util.List;

import static io.github.bitcoineducation.bitcoinjava.ExtendedKeyPrefixes.MAINNET_PREFIX;

@Component
public class CreateWalletService {
    private final List<AddressConfig> addressConfigs;
    private final ExtendedPubkeyService extendedPubkeyService;
    private final AddressSequentialGenerator addressSequentialGenerator;

    public CreateWalletService(List<AddressConfig> addressConfigs, ExtendedPubkeyService extendedPubkeyService, AddressSequentialGenerator addressSequentialGenerator) {
        this.addressConfigs = addressConfigs;
        this.extendedPubkeyService = extendedPubkeyService;
        this.addressSequentialGenerator = addressSequentialGenerator;
    }

    public Wallet create(String name, String password, String mnemonicSeedString) {
        MnemonicSeed mnemonicSeed = new MnemonicSeed(mnemonicSeedString);
        ExtendedPrivateKey masterKey = mnemonicSeed.toMasterKey(password, MAINNET_PREFIX.getPrivatePrefix());
        List<ExtendedPubkey> extendedPubkeys = addressConfigs.stream()
            .map(addressConfig -> extendedPubkeyService.create(masterKey, addressConfig.derivationPath(), addressConfig.addressType()))
            .toList();
        extendedPubkeys.forEach(this::generateAddresses);
        return new Wallet(name, extendedPubkeys);
    }

    private void generateAddresses(ExtendedPubkey extendedPubkey) {
        extendedPubkey.setAddresses(addressSequentialGenerator.generate(extendedPubkey.getKey(), extendedPubkey.getType()));
    }
}
