package com.byow.wallet.byow.gui.controllers;

import com.byow.wallet.byow.api.services.CreateWalletService;
import com.byow.wallet.byow.database.entities.WalletEntity;
import com.byow.wallet.byow.database.repositories.WalletRepository;
import com.byow.wallet.byow.domains.Wallet;
import com.byow.wallet.byow.gui.events.LoadedWalletEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.PasswordField;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class LoadWalletDialogController {
    @FXML
    private ButtonType cancel;

    @FXML
    private ButtonType ok;

    @FXML
    private PasswordField loadWalletPassword;

    @FXML
    private DialogPane dialogPane;

    private WalletEntity walletEntity;

    private final WalletRepository walletRepository;

    private final CreateWalletService createWalletService;

    private final ConfigurableApplicationContext context;

    public LoadWalletDialogController(WalletRepository walletRepository, CreateWalletService createWalletService, ConfigurableApplicationContext context) {
        this.walletRepository = walletRepository;
        this.createWalletService = createWalletService;
        this.context = context;
    }

    public void setWallet(WalletEntity wallet) {
        this.walletEntity = wallet;
    }

    public void initialize() {
        dialogPane.lookupButton(cancel)
            .addEventHandler(ActionEvent.ACTION, event -> dialogPane.getScene().getWindow().hide());
        dialogPane.lookupButton(ok)
            .addEventHandler(ActionEvent.ACTION, event -> loadWallet());
    }

    private void loadWallet() {
        walletEntity = walletRepository.findByName(walletEntity.getName());
        Wallet wallet = createWalletService.create(walletEntity.getName(), loadWalletPassword.getText(), walletEntity.getMnemonicSeed(), walletEntity.getCreatedAt(), walletEntity.getNumberOfGeneratedAddresses());
        context.publishEvent(new LoadedWalletEvent(this, wallet));
        dialogPane.getScene().getWindow().hide();
    }
}
