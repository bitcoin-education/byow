package com.byow.wallet.byow.gui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.layout.BorderPane;
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

    public MainWindowController(
        @Value("fxml/create_wallet_dialog.fxml") Resource createWalletDialog,
        ApplicationContext context
    ) {
        this.createWalletDialog = createWalletDialog;
        this.context = context;
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
