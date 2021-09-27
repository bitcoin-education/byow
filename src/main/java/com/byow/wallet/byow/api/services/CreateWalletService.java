package com.byow.wallet.byow.api.services;

import com.byow.wallet.byow.domains.Wallet;
import org.springframework.stereotype.Component;

@Component
public class CreateWalletService {
    public Wallet create(String name, String password, String mnemonicSeed) {
        return new Wallet(name);
    }
}
