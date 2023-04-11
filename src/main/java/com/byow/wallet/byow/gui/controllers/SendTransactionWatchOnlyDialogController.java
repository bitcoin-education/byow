package com.byow.wallet.byow.gui.controllers;

import com.byow.wallet.byow.domains.TransactionDialog;
import com.byow.wallet.byow.domains.TransactionDto;
import com.byow.wallet.byow.domains.node.Error;
import com.byow.wallet.byow.gui.services.AlertErrorService;
import com.byow.wallet.byow.gui.services.SendTransactionService;
import com.byow.wallet.byow.gui.services.SignedTransactionValidator;
import com.byow.wallet.byow.gui.services.UnsignedTransactionPayloadBuilder;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import io.github.bitcoineducation.bitcoinjava.Transaction;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;
import java.io.IOException;
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

    private final Resource openQRCodeCaptureDialog;

    private final Resource openQRCodeViewDialog;

    private final ConfigurableApplicationContext context;

    public SendTransactionWatchOnlyDialogController(
        UnsignedTransactionPayloadBuilder unsignedTransactionPayloadBuilder,
        SendTransactionService sendTransactionService,
        AlertErrorService alertErrorService,
        SignedTransactionValidator signedTransactionValidator,
        @Value("fxml/qr_code_capture.fxml") Resource openQRCodeCaptureDialog,
        @Value("fxml/qr_code_view.fxml") Resource openQRCodeViewDialog,
        ConfigurableApplicationContext context
    ) {
        this.unsignedTransactionPayloadBuilder = unsignedTransactionPayloadBuilder;
        this.sendTransactionService = sendTransactionService;
        this.alertErrorService = alertErrorService;
        this.signedTransactionValidator = signedTransactionValidator;
        this.openQRCodeCaptureDialog = openQRCodeCaptureDialog;
        this.openQRCodeViewDialog = openQRCodeViewDialog;
        this.context = context;
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

    public void captureQRCode() {
        openQRCodeCaptureDialog();
    }

    private void openQRCodeCaptureDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(this.dialogPane.getScene().getWindow());
        dialog.setTitle("Capture QR Code");

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(openQRCodeCaptureDialog.getURL(), null, null, context::getBean);
            dialog.getDialogPane().setContent(fxmlLoader.load());
            QRCodeCaptureController controller = fxmlLoader.getController();
            dialog.setOnShown(event -> {
                dialog.getDialogPane().getScene().getWindow().setOnCloseRequest(event1 -> {
                    controller.destroy();
                    dialog.hide();
                });
                dialog.getDialogPane().getScene().getWindow().setOnHidden(dialogEvent -> {
                    if (!controller.getDecodedQRCode().isBlank()) {
                        signedTransactionTextArea.setText(controller.getDecodedQRCode());
                    }
                });
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        dialog.show();
    }

    public void showQRCode() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(this.dialogPane.getScene().getWindow());
        dialog.setTitle("QR Code");

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(openQRCodeViewDialog.getURL(), null, null, context::getBean);
            dialog.getDialogPane().setContent(fxmlLoader.load());
            QRCodeViewController controller = fxmlLoader.getController();
            controller.setImage(buildImage());
            dialog.setOnShown(event -> {
                dialog.getDialogPane().getScene().getWindow().setOnCloseRequest(event1 -> {
                    dialog.hide();
                    controller.setImage(null);
                });
            });
        } catch (IOException | WriterException e) {
            throw new RuntimeException(e);
        }
        dialog.show();
    }

    private WritableImage buildImage() throws WriterException {
        BitMatrix bitMatrix = new MultiFormatWriter().encode(unsignedTransactionJsonTextArea.getText(), BarcodeFormat.QR_CODE, 300, 300);
        BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
        return SwingFXUtils.toFXImage(bufferedImage, null);
    }
}
