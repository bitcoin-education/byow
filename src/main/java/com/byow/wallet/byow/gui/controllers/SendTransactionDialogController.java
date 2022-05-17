package com.byow.wallet.byow.gui.controllers;

import com.byow.wallet.byow.domains.TransactionDialog;
import com.byow.wallet.byow.domains.TransactionDto;
import com.byow.wallet.byow.gui.services.SendTransactionService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import org.springframework.stereotype.Component;

@Component
public class SendTransactionDialogController {
    @FXML
    private DialogPane dialogPane;
    @FXML
    private Label amountToSendDialog;
    @FXML
    private Label totalFees;
    @FXML
    private Label total;
    @FXML
    private Label feeRate;
    @FXML
    private Label addressToSendDialog;
    @FXML
    private PasswordField walletPassword;
    @FXML
    private ButtonType ok;
    @FXML
    private ButtonType cancel;

    private TransactionDto transactionDto;

    private final SendTransactionService sendTransactionService;

    public SendTransactionDialogController(SendTransactionService sendTransactionService) {
        this.sendTransactionService = sendTransactionService;
    }

    public void setTransaction(TransactionDto transactionDto) {
        this.transactionDto = transactionDto;
        TransactionDialog transactionDialog = TransactionDialog.from(transactionDto);
        amountToSendDialog.setText(transactionDialog.amountToSend());
        totalFees.setText(transactionDialog.totalFee());
        total.setText(transactionDialog.total());
        feeRate.setText(transactionDialog.feeRate().toString().concat(" BTC/kvByte"));
        addressToSendDialog.setText(transactionDialog.addressToSend());
    }

    public void initialize() {
        dialogPane.lookupButton(ok)
                .addEventHandler(ActionEvent.ACTION, event -> signAndSendTransaction());
        dialogPane.lookupButton(cancel)
            .addEventHandler(ActionEvent.ACTION, event -> dialogPane.getScene().getWindow().hide());
    }

    private void signAndSendTransaction() {
        sendTransactionService.signAndSend(transactionDto, walletPassword.getText());
        dialogPane.getScene().getWindow().hide();
    }
}
