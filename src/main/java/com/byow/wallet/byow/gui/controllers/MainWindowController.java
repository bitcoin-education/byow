package com.byow.wallet.byow.gui.controllers;

import com.byow.wallet.byow.observables.CurrentWallet;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class MainWindowController {
    @FXML
    private BorderPane borderPane;
    private final Resource createWalletDialog;
    private final ApplicationContext context;
    private final CurrentWallet currentWallet;

    public MainWindowController(
        @Value("fxml/create_wallet_dialog.fxml") Resource createWalletDialog,
        ApplicationContext context,
        CurrentWallet currentWallet
    ) {
        this.createWalletDialog = createWalletDialog;
        this.context = context;
        this.currentWallet = currentWallet;
    }

    public void initialize() {
        currentWallet.nameProperty().addListener((observable, oldValue, newValue) -> {
            Stage stage = (Stage) borderPane.getScene().getWindow();
            stage.setTitle("BYOW Wallet - ".concat(newValue));
        });
    }

    public void openCreateWalletDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(this.borderPane.getScene().getWindow());
        dialog.setTitle("Create New Wallet");
        dialog.setOnShown(event -> dialog.getDialogPane().getScene().getWindow().setOnCloseRequest(event2 -> dialog.hide()));

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(createWalletDialog.getURL(), null, null, context::getBean);
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        dialog.show();
    }
}
