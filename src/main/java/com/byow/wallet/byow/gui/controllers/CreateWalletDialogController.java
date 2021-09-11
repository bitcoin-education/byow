package com.byow.wallet.byow.gui.controllers;

import com.byow.wallet.byow.domains.Wallet;
import com.byow.wallet.byow.api.services.CreateWalletService;
import com.byow.wallet.byow.api.services.MnemonicSeedService;
import com.byow.wallet.byow.gui.events.CreatedWalletEvent;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;

@Component
public class CreateWalletDialogController {
    @FXML
    private TextField password;

    @FXML
    private  TextField name;

    @FXML
    private ButtonType ok;

    @FXML
    private  ButtonType cancel;

    @FXML
    private DialogPane dialogPane;

    @FXML
    private TextArea mnemonicSeed;

    private BooleanBinding allRequiredInputsAreFull;

    private final MnemonicSeedService mnemonicSeedService;

    private final ConfigurableApplicationContext context;

    private final CreateWalletService createWalletService;

    public CreateWalletDialogController(MnemonicSeedService mnemonicSeedService, ConfigurableApplicationContext context, CreateWalletService createWalletService) {
        this.mnemonicSeedService = mnemonicSeedService;
        this.context = context;
        this.createWalletService = createWalletService;
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

    public BooleanBinding getAllRequiredInputsAreFull() {
        return allRequiredInputsAreFull;
    }

    private void createWallet() {
        dialogPane.getScene().getWindow().hide();
        Wallet wallet = createWalletService.create(this.name.getText(), this.password.getText(), this.mnemonicSeed.getText());
        this.context.publishEvent(new CreatedWalletEvent(this, wallet));
    }

    public void createMnemonicSeed() throws FileNotFoundException {
        mnemonicSeed.setText(mnemonicSeedService.create());
    }
}
