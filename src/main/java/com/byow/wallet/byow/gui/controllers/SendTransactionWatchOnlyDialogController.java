package com.byow.wallet.byow.gui.controllers;

import com.byow.wallet.byow.domains.TransactionDialog;
import com.byow.wallet.byow.domains.TransactionDto;
import com.byow.wallet.byow.domains.node.Error;
import com.byow.wallet.byow.gui.services.AlertErrorService;
import com.byow.wallet.byow.gui.services.SendTransactionService;
import com.byow.wallet.byow.gui.services.SignedTransactionValidator;
import com.byow.wallet.byow.gui.services.UnsignedTransactionPayloadBuilder;
import io.github.bitcoineducation.bitcoinjava.Transaction;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import org.springframework.stereotype.Component;

import java.util.concurrent.Future;

import static com.byow.wallet.byow.domains.node.ErrorMessages.INVALID_TRANSACTION;
import static com.byow.wallet.byow.utils.TransactionDeserializer.deserializeTransaction;

@Component
public class SendTransactionWatchOnlyDialogController {
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
    private ButtonType ok;
    @FXML
    private ButtonType cancel;
    @FXML
    private TextArea unsignedTransactionJsonTextArea;
    @FXML
    private TextArea signedTransactionTextArea;

    private TransactionDto transactionDto;

    private final UnsignedTransactionPayloadBuilder unsignedTransactionPayloadBuilder;

    private final SendTransactionService sendTransactionService;

    private final AlertErrorService alertErrorService;

    private final SignedTransactionValidator signedTransactionValidator;

    public SendTransactionWatchOnlyDialogController(
        UnsignedTransactionPayloadBuilder unsignedTransactionPayloadBuilder,
        SendTransactionService sendTransactionService,
        AlertErrorService alertErrorService,
        SignedTransactionValidator signedTransactionValidator
    ) {
        this.unsignedTransactionPayloadBuilder = unsignedTransactionPayloadBuilder;
        this.sendTransactionService = sendTransactionService;
        this.alertErrorService = alertErrorService;
        this.signedTransactionValidator = signedTransactionValidator;
    }

    public void setTransaction(TransactionDto transactionDto) {
        this.transactionDto = transactionDto;
        TransactionDialog transactionDialog = TransactionDialog.from(transactionDto);
        amountToSendDialog.setText(transactionDialog.amountToSend());
        totalFees.setText(transactionDialog.totalFee());
        total.setText(transactionDialog.total());
        feeRate.setText(transactionDialog.feeRate().toString().concat(" BTC/kvByte"));
        addressToSendDialog.setText(transactionDialog.addressToSend());
        String unsignedTransactionPayload = unsignedTransactionPayloadBuilder.build(transactionDto);
        unsignedTransactionJsonTextArea.setText(unsignedTransactionPayload);
    }

    public void initialize() {
        dialogPane.lookupButton(ok)
            .addEventHandler(ActionEvent.ACTION, event -> sendTransaction());
        dialogPane.lookupButton(cancel)
            .addEventHandler(ActionEvent.ACTION, event -> dialogPane.getScene().getWindow().hide());
    }

    private void sendTransaction() {
        Transaction signedTransaction;
        try {
            signedTransaction = deserializeTransaction(signedTransactionTextArea.getText());
            signedTransactionValidator.validateSignedTransaction(signedTransaction, transactionDto.transaction());
        } catch(Exception e) {
            alertErrorService.alertError(INVALID_TRANSACTION);
            return;
        }
        Future<Error> result = sendTransactionService.send(new TransactionDto(
            signedTransaction,
            transactionDto.feeRate(),
            transactionDto.amountToSend(),
            transactionDto.totalActualFee(),
            transactionDto.totalCalculatedFee(),
            transactionDto.totalSpent(),
            transactionDto.address(),
            transactionDto.selectedUtxos()
        ));
        alertErrorService.handleError(result);
        dialogPane.getScene().getWindow().hide();
    }
}
