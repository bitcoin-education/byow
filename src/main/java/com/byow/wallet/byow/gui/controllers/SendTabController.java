package com.byow.wallet.byow.gui.controllers;

import com.byow.wallet.byow.domains.TransactionDto;
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

    private final CreateTransactionService createTransactionService;

    private final Resource dialogFxml;

    private final ApplicationContext context;

    @FXML
    private TextField addressToSend;

    @FXML
    private TextField amountToSend;

    public SendTabController(
        @Value("fxml/send_tab.fxml") Resource fxml,
        ApplicationContext context,
        CurrentWallet currentWallet,
        CreateTransactionService createTransactionService,
        @Value("fxml/send_transaction_dialog.fxml") Resource dialogFxml
    ) throws IOException {
        this.currentWallet = currentWallet;
        this.createTransactionService = createTransactionService;
        this.dialogFxml = dialogFxml;
        this.context = context;
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
        TransactionDto transactionDto = createTransactionService.create(addressToSend.getText(), amount);
        openDialog(transactionDto);
        addressToSend.clear();
        amountToSend.clear();
    }

    private void openDialog(TransactionDto transactionDto) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(getTabPane().getScene().getWindow());
        dialog.setTitle("Send transaction");
        dialog.setOnShown(event -> dialog.getDialogPane().getScene().getWindow().setOnCloseRequest(event1 -> dialog.hide()));

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(dialogFxml.getURL(), null, null, context::getBean);
            dialog.getDialogPane().setContent(fxmlLoader.load());
            SendTransactionDialogController sendTransactionDialogController = fxmlLoader.getController();
            sendTransactionDialogController.setTransaction(transactionDto);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        dialog.show();
    }
}
