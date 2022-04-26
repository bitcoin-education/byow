package com.byow.wallet.byow.gui.controllers;

import com.byow.wallet.byow.domains.TransactionDialog;
import com.byow.wallet.byow.domains.TransactionDto;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
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

    private TransactionDto transactionDto;

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
        dialogPane.lookupButton(cancel)
            .addEventHandler(ActionEvent.ACTION, event -> dialogPane.getScene().getWindow().hide());
    }

}