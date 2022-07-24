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

    private final LoadMenu loadMenu;

    private final int initialNumberOfGeneratedAddresses;

    public SaveWalletService(
        WalletRepository walletRepository,
        LoadMenu loadMenu,
        @Qualifier("initialNumberOfGeneratedAddresses") int initialNumberOfGeneratedAddresses
    ) {
        this.walletRepository = walletRepository;
        this.loadMenu = loadMenu;
        this.initialNumberOfGeneratedAddresses = initialNumberOfGeneratedAddresses;
    }

    public void saveWallet(Wallet wallet) {
        WalletEntity walletEntity = new WalletEntity(
            wallet.name(),
            wallet.mnemonicSeed(),
            wallet.createdAt(),
            initialNumberOfGeneratedAddresses
        );
        walletRepository.save(walletEntity);
        Platform.runLater(() -> loadMenu.getObservableMenuItems().add(walletEntity));
    }
}
