package com.byow.wallet.byow.gui.controllers;

import com.byow.wallet.byow.domains.TransactionDialog;
import com.byow.wallet.byow.domains.TransactionDto;
import com.byow.wallet.byow.gui.services.AlertErrorService;
import com.byow.wallet.byow.gui.services.SignAndSendTransactionService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.springframework.stereotype.Component;
import com.byow.wallet.byow.domains.Error;

import java.util.concurrent.Future;

@Component
public class SendTransactionDialogController extends DialogPane {
    @FXML
    private DialogPane dialogPane;
    @FXML
    private ButtonType ok;
    @FXML
    private ButtonType cancel;
    @FXML
    private Label amountToSendDialog;
    @FXML
    private Label totalFee;
    @FXML
    private Label total;
    @FXML
    private Label feeRate;
    @FXML
    private Label addressToSendDialog;
    @FXML
    private PasswordField sendTransactionPassword;

    private final SignAndSendTransactionService signAndSendTransactionService;

    private TransactionDto transactionDto;

    private final AlertErrorService alertErrorService;

    public SendTransactionDialogController(SignAndSendTransactionService signAndSendTransactionService, AlertErrorService alertErrorService) {
        this.signAndSendTransactionService = signAndSendTransactionService;
        this.alertErrorService = alertErrorService;
    }

    public void setTransaction(TransactionDto transactionDto) {
        this.transactionDto = transactionDto;
        TransactionDialog transactionDialog = TransactionDialog.from(transactionDto);
        amountToSendDialog.setText(transactionDialog.amountToSend());
        totalFee.setText(transactionDialog.totalFee());
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
        Future<Error> result = signAndSendTransactionService.signAndSend(transactionDto, sendTransactionPassword.getText());
        alertErrorService.handleError(result);
        dialogPane.getScene().getWindow().hide();
    }

}
