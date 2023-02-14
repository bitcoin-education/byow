package com.byow.wallet.byow.gui.controllers;

import com.byow.wallet.byow.domains.TransactionDto;
import com.byow.wallet.byow.gui.exceptions.CreateTransactionException;
import com.byow.wallet.byow.gui.services.AlertErrorService;
import com.byow.wallet.byow.gui.services.CreateTransactionService;
import com.byow.wallet.byow.observables.CurrentWallet;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;

@Component
public class SendTabController extends Tab {
    private final CurrentWallet currentWallet;

    @FXML
    private TextField addressToSend;

    @FXML
    private TextField amountToSend;

    private final CreateTransactionService createTransactionService;

    private final Resource sendTransactionDialogFxml;

    private final Resource sendTransactionWatchOnlyDialogFxml;

    private final ApplicationContext context;

    private final AlertErrorService alertErrorService;

    public SendTabController(
        @Value("fxml/send_tab.fxml") Resource fxml,
        ApplicationContext context,
        CurrentWallet currentWallet,
        CreateTransactionService createTransactionService,
        @Value("fxml/send_transaction_dialog.fxml") Resource sendTransactionDialogFxml,
        @Value("fxml/send_transaction_watch_only_dialog.fxml") Resource sendTransactionWatchOnlyDialogFxml,
        AlertErrorService alertErrorService
    ) throws IOException {
        this.currentWallet = currentWallet;
        this.createTransactionService = createTransactionService;
        this.sendTransactionDialogFxml = sendTransactionDialogFxml;
        this.context = context;
        this.sendTransactionWatchOnlyDialogFxml = sendTransactionWatchOnlyDialogFxml;
        this.alertErrorService = alertErrorService;
        FXMLLoader fxmlLoader = new FXMLLoader(
            fxml.getURL(),
            null,
            null,
            context::getBean
        );
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);
        fxmlLoader.load();
    }

    public void send() {
        BigDecimal amount = new BigDecimal(amountToSend.getText());
        try {
            TransactionDto transactionDto = createTransactionService.create(addressToSend.getText(), amount);
            openDialog(transactionDto);
        } catch (CreateTransactionException exception) {
            alertErrorService.alertError(exception.getMessage());
        }
        addressToSend.clear();
        amountToSend.clear();
    }

    private void openDialog(TransactionDto transactionDto) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(getTabPane().getScene().getWindow());
        dialog.setTitle("Send transaction");
        dialog.setOnShown(event -> dialog.getDialogPane().getScene().getWindow().setOnCloseRequest(event1 -> dialog.hide()));

        try {
            loadDialog(transactionDto, dialog);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        dialog.show();
    }

    private void loadDialog(TransactionDto transactionDto, Dialog<ButtonType> dialog) throws IOException {
        if (currentWallet.isWatchOnly()) {
            FXMLLoader fxmlLoader = new FXMLLoader(sendTransactionWatchOnlyDialogFxml.getURL(), null, null, context::getBean);
            dialog.getDialogPane().setContent(fxmlLoader.load());
            SendTransactionWatchOnlyDialogController sendTransactionWatchOnlyDialogController = fxmlLoader.getController();
            sendTransactionWatchOnlyDialogController.setTransaction(transactionDto);
            return;
        }
        FXMLLoader fxmlLoader = new FXMLLoader(sendTransactionDialogFxml.getURL(), null, null, context::getBean);
        dialog.getDialogPane().setContent(fxmlLoader.load());
        SendTransactionDialogController sendTransactionDialogController = fxmlLoader.getController();
        sendTransactionDialogController.setTransaction(transactionDto);
    }
}
