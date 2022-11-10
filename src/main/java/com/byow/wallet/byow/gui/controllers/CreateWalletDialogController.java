package com.byow.wallet.byow.gui.controllers;

import com.byow.wallet.byow.api.services.MnemonicSeedService;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;

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

    public CreateWalletDialogController(MnemonicSeedService mnemonicSeedService) {
        this.mnemonicSeedService = mnemonicSeedService;
    }

    public void createMnemonicSeed() throws FileNotFoundException {
        mnemonicSeed.setText(mnemonicSeedService.create());
    }

    private void createWallet() {
        dialogPane.getScene().getWindow().hide();
    }

    public BooleanBinding getAllRequiredInputsAreFull() {
        return allRequiredInputsAreFull;
    }

    public BooleanBinding allRequiredInputsAreFullProperty() {
        return allRequiredInputsAreFull;
    }
}
