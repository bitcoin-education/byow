package com.byow.wallet.byow.api.services;

import com.byow.wallet.byow.domains.ExtendedPubkey;
import com.byow.wallet.byow.domains.Wallet;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CreateWalletService {
    private final ExtendedPubkeyService extendedPubkeyService;

    private final AddressSequentialGenerator addressSequentialGenerator;

    public CreateWalletService(ExtendedPubkeyService extendedPubkeyService, AddressSequentialGenerator addressSequentialGenerator) {
        this.extendedPubkeyService = extendedPubkeyService;
        this.addressSequentialGenerator = addressSequentialGenerator;
    }

    public Wallet create(String name, String password, String mnemonicSeedString) {
        List<ExtendedPubkey> extendedPubkeys = extendedPubkeyService.create(mnemonicSeedString, password);
        extendedPubkeys.forEach(this::generateAddresses);
        return new Wallet(name, extendedPubkeys);
    }

    private void generateAddresses(ExtendedPubkey extendedPubkey) {
        extendedPubkey.setAddresses(addressSequentialGenerator.generate(extendedPubkey.getKey(), extendedPubkey.getType()));
    }
}
