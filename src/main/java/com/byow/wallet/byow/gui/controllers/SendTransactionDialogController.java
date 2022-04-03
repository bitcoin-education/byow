package com.byow.wallet.byow.gui.controllers;

import com.byow.wallet.byow.domains.TransactionDialog;
import com.byow.wallet.byow.domains.TransactionDto;
import com.byow.wallet.byow.gui.services.SignAndSendTransactionService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import org.springframework.stereotype.Component;

@Component
public class SendTransactionDialogController extends DialogPane {
    @FXML
    private DialogPane dialogPane;
    @FXML
    private ButtonType ok;
    @FXML
    private ButtonType cancel;
    @FXML
    private Label amountToSend;
    @FXML
    private Label totalFee;
    @FXML
    private Label total;
    @FXML
    private Label feeRate;
    @FXML
    private Label addressToSend;
    @FXML
    private PasswordField sendTransactionPassword;

    private final SignAndSendTransactionService signAndSendTransactionService;

    private TransactionDto transactionDto;

    public SendTransactionDialogController(SignAndSendTransactionService signAndSendTransactionService) {
        this.signAndSendTransactionService = signAndSendTransactionService;
    }

    public void setTransaction(TransactionDto transactionDto) {
        this.transactionDto = transactionDto;
        TransactionDialog transactionDialog = TransactionDialog.from(transactionDto);
        amountToSend.setText(transactionDialog.amountToSend());
        totalFee.setText(transactionDialog.totalFee());
        total.setText(transactionDialog.total());
        feeRate.setText(transactionDialog.feeRate().toString().concat(" BTC/KvByte"));
        addressToSend.setText(transactionDialog.addressToSend());
    }

    public void initialize() {
        dialogPane.lookupButton(ok)
            .addEventHandler(ActionEvent.ACTION, event -> signAndSendTransaction());
        dialogPane.lookupButton(cancel)
            .addEventHandler(ActionEvent.ACTION, event -> dialogPane.getScene().getWindow().hide());
    }

    private void signAndSendTransaction() {
        signAndSendTransactionService.signAndSend(transactionDto.transaction(), sendTransactionPassword.getText(), transactionDto.selectedUtxos());
        dialogPane.getScene().getWindow().hide();
    }
}
