package com.byow.wallet.byow.database.services;

import com.byow.wallet.byow.database.entities.WalletEntity;
import com.byow.wallet.byow.database.repositories.WalletRepository;
import com.byow.wallet.byow.domains.Wallet;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class SaveWalletService {
    private final WalletRepository walletRepository;

    private final int initialNumberOfGeneratedAddresses;

    public SaveWalletService(
        WalletRepository walletRepository,
        @Qualifier("initialNumberOfGeneratedAddresses") int initialNumberOfGeneratedAddresses
    ) {
        this.walletRepository = walletRepository;
        this.initialNumberOfGeneratedAddresses = initialNumberOfGeneratedAddresses;
    }

    public void saveWallet(Wallet wallet) {
        WalletEntity walletEntity = new WalletEntity(
            wallet.name(),
            wallet.mnemonicSeed(),
            initialNumberOfGeneratedAddresses,
            wallet.createdAt()
        );
        walletRepository.save(walletEntity);
    }
}
