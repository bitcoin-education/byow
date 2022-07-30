package com.byow.wallet.byow.gui.controllers;

import com.byow.wallet.byow.database.entities.WalletEntity;
import com.byow.wallet.byow.observables.CurrentWallet;
import com.byow.wallet.byow.observables.LoadMenu;
import javafx.collections.SetChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.stream.Collectors;

@Component
public class MainWindowController {
    @FXML
    private Menu loadMenuFxml;

    @FXML
    private BorderPane borderPane;

    private final Resource createWalletDialog;

    private final Resource loadWalletDialog;

    private final ApplicationContext context;

    private final CurrentWallet currentWallet;

    private final LoadMenu loadMenu;

    public MainWindowController(
        @Value("fxml/create_wallet_dialog.fxml") Resource createWalletDialog,
        @Value("fxml/load_wallet_dialog.fxml") Resource loadWalletDialog,
        ApplicationContext context,
        CurrentWallet currentWallet,
        LoadMenu loadMenu
    ) {
        this.createWalletDialog = createWalletDialog;
        this.loadWalletDialog = loadWalletDialog;
        this.context = context;
        this.currentWallet = currentWallet;
        this.loadMenu = loadMenu;
    }

    public void initialize() {
        currentWallet.nameProperty().addListener(((observable, oldValue, newValue) -> {
            Stage stage = (Stage) borderPane.getScene().getWindow();
            stage.setTitle("BYOW Wallet - ".concat(newValue));
        }));
        loadMenu.getObservableMenuItems().addListener((SetChangeListener<WalletEntity>) change -> {
                WalletEntity walletEntity = change.getElementAdded();
                if (!menuContainsWallet(walletEntity)) {
                    MenuItem menuItem = new MenuItem(walletEntity.getName());
                    menuItem.setOnAction(click -> openLoadWalletDialog(walletEntity));
                    loadMenuFxml.getItems().addAll(menuItem);
                }
            }
        );
    }

    private void openLoadWalletDialog(WalletEntity walletEntity) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(this.borderPane.getScene().getWindow());
        dialog.setTitle("Load Wallet");
        dialog.setOnShown(event -> dialog.getDialogPane().getScene().getWindow().setOnCloseRequest(event1 -> dialog.hide()));

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(loadWalletDialog.getURL(), null, null, context::getBean);
            dialog.getDialogPane().setContent(fxmlLoader.load());
            LoadWalletDialogController controller = fxmlLoader.getController();
            controller.setWallet(walletEntity);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        dialog.show();
    }

    private boolean menuContainsWallet(WalletEntity walletEntity) {
        return loadMenuFxml.getItems()
            .stream()
            .map(MenuItem::getText)
            .collect(Collectors.toSet())
            .contains(walletEntity.getName());
    }

    public void openCreateWalletDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(this.borderPane.getScene().getWindow());
        dialog.setTitle("Create New Wallet");
        dialog.setOnShown(event -> dialog.getDialogPane().getScene().getWindow().setOnCloseRequest(event1 -> dialog.hide()));

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(createWalletDialog.getURL(), null, null, context::getBean);
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        dialog.show();
    }
}
