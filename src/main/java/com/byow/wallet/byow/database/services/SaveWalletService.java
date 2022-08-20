package com.byow.wallet.byow.database.services;

import com.byow.wallet.byow.database.entities.WalletEntity;
import com.byow.wallet.byow.database.repositories.WalletRepository;
import com.byow.wallet.byow.domains.Wallet;
import com.byow.wallet.byow.observables.LoadMenu;
import javafx.application.Platform;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class SaveWalletService {
    private final WalletRepository walletRepository;

    private final int initialNumberOfGeneratedAddresses;

    private final LoadMenu loadMenu;

    public SaveWalletService(
        WalletRepository walletRepository,
        @Qualifier("initialNumberOfGeneratedAddresses") int initialNumberOfGeneratedAddresses,
        LoadMenu loadMenu
    ) {
        this.walletRepository = walletRepository;
        this.initialNumberOfGeneratedAddresses = initialNumberOfGeneratedAddresses;
        this.loadMenu = loadMenu;
    }

    public void saveWallet(Wallet wallet) {
        WalletEntity walletEntity = new WalletEntity(
            wallet.name(),
            wallet.mnemonicSeed(),
            initialNumberOfGeneratedAddresses,
            wallet.createdAt()
        );
        walletRepository.save(walletEntity);
        Platform.runLater(() -> loadMenu.add(walletEntity));
    }
}
