package com.byow.wallet.byow.database.services;

import com.byow.wallet.byow.database.repositories.WalletRepository;
import org.springframework.stereotype.Service;

@Service
public class ValidateWalletService {
    private final WalletRepository walletRepository;

    public ValidateWalletService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    public boolean walletExists(String name) {
        return walletRepository.existsByName(name);
    }
}
