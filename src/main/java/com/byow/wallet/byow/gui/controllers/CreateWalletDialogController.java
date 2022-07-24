package com.byow.wallet.byow.gui.controllers;

import com.byow.wallet.byow.api.services.CreateWalletService;
import com.byow.wallet.byow.api.services.MnemonicSeedService;
import com.byow.wallet.byow.database.services.ValidateWalletService;
import com.byow.wallet.byow.domains.Wallet;
import com.byow.wallet.byow.gui.events.CreatedWalletEvent;
import com.byow.wallet.byow.gui.services.AlertErrorService;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.util.Date;

import static com.byow.wallet.byow.domains.node.ErrorMessages.WALLET_NAME_ALREADY_EXISTS;

@Component
public class CreateWalletDialogController {
    @FXML
    private ButtonType cancel;

    @FXML
    private ButtonType ok;

    @FXML
    private PasswordField password;

    @FXML
    private TextField name;

    @FXML
    private DialogPane dialogPane;

    @FXML
    private TextArea mnemonicSeed;

    private final MnemonicSeedService mnemonicSeedService;

    private BooleanBinding allRequiredInputsAreFull;

    private final CreateWalletService createWalletService;

    private final ConfigurableApplicationContext context;

    private final int initialNumberOfGeneratedAddresses;

    private final ValidateWalletService validateWalletService;

    private final AlertErrorService alertErrorService;

    public CreateWalletDialogController(
        MnemonicSeedService mnemonicSeedService,
        CreateWalletService createWalletService,
        ConfigurableApplicationContext context,
        @Qualifier("initialNumberOfGeneratedAddresses") int initialNumberOfGeneratedAddresses,
        ValidateWalletService validateWalletService,
        AlertErrorService alertErrorService
    ) {
        this.mnemonicSeedService = mnemonicSeedService;
        this.createWalletService = createWalletService;
        this.context = context;
        this.initialNumberOfGeneratedAddresses = initialNumberOfGeneratedAddresses;
        this.validateWalletService = validateWalletService;
        this.alertErrorService = alertErrorService;
    }

    public void createMnemonicSeed() throws FileNotFoundException {
        mnemonicSeed.setText(mnemonicSeedService.create());
    }

    public void initialize() {
        dialogPane.lookupButton(cancel)
            .addEventHandler(ActionEvent.ACTION, event -> dialogPane.getScene().getWindow().hide());
        allRequiredInputsAreFull = new BooleanBinding() {
            {
                bind(name.textProperty(), mnemonicSeed.textProperty());
            }

            @Override
            protected boolean computeValue() {
                return !(name.getText().trim().isEmpty() || mnemonicSeed.getText().trim().isEmpty());
            }
        };
        dialogPane.lookupButton(ok)
            .disableProperty()
            .bind(getAllRequiredInputsAreFull().not());
        dialogPane.lookupButton(ok)
            .addEventHandler(ActionEvent.ACTION, event -> createWallet());
    }

    private void createWallet() {
        if (validateWalletService.walletExists(name.getText())) {
            alertErrorService.alertError(WALLET_NAME_ALREADY_EXISTS);
            return;
        }
        Wallet wallet = createWalletService.create(name.getText(), password.getText(), mnemonicSeed.getText(), new Date(), initialNumberOfGeneratedAddresses);
        this.context.publishEvent(new CreatedWalletEvent(this, wallet));
        dialogPane.getScene().getWindow().hide();
    }

    public BooleanBinding getAllRequiredInputsAreFull() {
        return allRequiredInputsAreFull;
    }

}
