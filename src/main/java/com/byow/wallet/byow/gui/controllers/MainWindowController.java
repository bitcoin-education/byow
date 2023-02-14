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

    private final Resource importWalletDialog;

    private final Resource importWatchOnlyWalletDialog;

    private final Resource exportExtendedPubkeysDialog;

    private final ApplicationContext context;

    private final CurrentWallet currentWallet;

    private final LoadMenu loadMenu;

    public MainWindowController(
        @Value("fxml/create_wallet_dialog.fxml") Resource createWalletDialog,
        @Value("fxml/load_wallet_dialog.fxml") Resource loadWalletDialog,
        @Value("fxml/import_wallet_dialog.fxml") Resource importWalletDialog,
        @Value("fxml/import_watch_only_wallet_dialog.fxml") Resource importWatchOnlyWalletDialog,
        @Value("fxml/export_extended_pubkeys_dialog.fxml")Resource exportExtendedPubkeysDialog,
        ApplicationContext context,
        CurrentWallet currentWallet,
        LoadMenu loadMenu
    ) {
        this.createWalletDialog = createWalletDialog;
        this.loadWalletDialog = loadWalletDialog;
        this.importWalletDialog = importWalletDialog;
        this.importWatchOnlyWalletDialog = importWatchOnlyWalletDialog;
        this.exportExtendedPubkeysDialog = exportExtendedPubkeysDialog;
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
            WalletEntity wallet = change.getElementAdded();
            if (!menuContainsWallet(wallet)) {
                MenuItem menuItem = new MenuItem(wallet.getName());
                menuItem.setOnAction(click -> openLoadWalletDialog(wallet));
                loadMenuFxml.getItems().addAll(menuItem);
            }
        });
    }

    private void openLoadWalletDialog(WalletEntity wallet) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(this.borderPane.getScene().getWindow());
        dialog.setTitle("Load Wallet");
        dialog.setOnShown(event -> dialog.getDialogPane().getScene().getWindow().setOnCloseRequest(event1 -> dialog.hide()));

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(loadWalletDialog.getURL(), null, null, context::getBean);
            dialog.getDialogPane().setContent(fxmlLoader.load());
            LoadWalletDialogController controller = fxmlLoader.getController();
            controller.setWallet(wallet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        dialog.show();
    }

    private boolean menuContainsWallet(WalletEntity wallet) {
        return loadMenuFxml.getItems()
            .stream()
            .map(MenuItem::getText)
            .collect(Collectors.toSet())
            .contains(wallet.getName());
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

    public void openImportWalletDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(this.borderPane.getScene().getWindow());
        dialog.setTitle("Import Wallet");
        dialog.setOnShown(event -> dialog.getDialogPane().getScene().getWindow().setOnCloseRequest(event1 -> dialog.hide()));

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(importWalletDialog.getURL(), null, null, context::getBean);
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        dialog.show();
    }

    public void openImportWatchOnlyWalletDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(this.borderPane.getScene().getWindow());
        dialog.setTitle("Import Watch-only Wallet");
        dialog.setOnShown(event -> dialog.getDialogPane().getScene().getWindow().setOnCloseRequest(event1 -> dialog.hide()));

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(importWatchOnlyWalletDialog.getURL(), null, null, context::getBean);
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        dialog.show();
    }

    public void openExportExtendedPubkeysDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(this.borderPane.getScene().getWindow());
        dialog.setTitle("Export Extended Pubkeys");
        dialog.setOnShown(event -> dialog.getDialogPane().getScene().getWindow().setOnCloseRequest(event1 -> dialog.hide()));

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(exportExtendedPubkeysDialog.getURL(), null, null, context::getBean);
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        dialog.show();
    }
}
