package com.byow.wallet.byow.gui.controllers;

import com.byow.wallet.byow.api.services.CreateWalletService;
import com.byow.wallet.byow.api.services.CreateWatchOnlyWalletService;
import com.byow.wallet.byow.database.entities.WalletEntity;
import com.byow.wallet.byow.database.repositories.WalletRepository;
import com.byow.wallet.byow.domains.Wallet;
import com.byow.wallet.byow.gui.events.LoadedWalletEvent;
import com.byow.wallet.byow.gui.services.AlertErrorService;
import com.byow.wallet.byow.gui.services.WatchOnlyPasswordService;
import com.byow.wallet.byow.utils.ExtendedPubkeysSerializer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.PasswordField;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import javax.security.auth.login.LoginException;

import static com.byow.wallet.byow.domains.node.ErrorMessages.INVALID_WATCH_ONLY_PASSWORD;

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

    private final CreateWatchOnlyWalletService createWatchOnlyWalletService;

    private final ConfigurableApplicationContext context;

    private final WatchOnlyPasswordService watchOnlyPasswordService;

    private final AlertErrorService alertErrorService;

    public LoadWalletDialogController(
        WalletRepository walletRepository,
        CreateWalletService createWalletService,
        CreateWatchOnlyWalletService createWatchOnlyWalletService,
        ConfigurableApplicationContext context,
        WatchOnlyPasswordService watchOnlyPasswordService,
        AlertErrorService alertErrorService
    ) {
        this.walletRepository = walletRepository;
        this.createWalletService = createWalletService;
        this.createWatchOnlyWalletService = createWatchOnlyWalletService;
        this.context = context;
        this.watchOnlyPasswordService = watchOnlyPasswordService;
        this.alertErrorService = alertErrorService;
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
        try {
            walletEntity = walletRepository.findByName(walletEntity.getName());
            Wallet wallet = createWallet();
            context.publishEvent(new LoadedWalletEvent(this, wallet));
            dialogPane.getScene().getWindow().hide();
        } catch (LoginException exception) {
            alertErrorService.alertError(INVALID_WATCH_ONLY_PASSWORD);
        }
    }

    private Wallet createWallet() throws LoginException {
        if (walletEntity.isWatchOnly()) {
            watchOnlyPasswordService.validate(walletEntity, loadWalletPassword.getText());
            return createWatchOnlyWalletService.create(
                walletEntity.getName(),
                ExtendedPubkeysSerializer.serializeExtendedPubkeyEntities(walletEntity.getExtendedPubkeys()),
                walletEntity.getCreatedAt(),
                walletEntity.getNumberOfGeneratedAddresses()
            );
        }
        return createWalletService.create(walletEntity.getName(), loadWalletPassword.getText(), walletEntity.getMnemonicSeed(), walletEntity.getCreatedAt(), walletEntity.getNumberOfGeneratedAddresses());
    }

}
