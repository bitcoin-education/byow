package com.byow.wallet.byow.gui.controllers;

import com.byow.wallet.byow.api.services.CreateWatchOnlyWalletService;
import com.byow.wallet.byow.database.services.ValidateWalletService;
import com.byow.wallet.byow.domains.Wallet;
import com.byow.wallet.byow.gui.events.ImportedWatchOnlyWalletEvent;
import com.byow.wallet.byow.gui.services.AlertErrorService;
import com.byow.wallet.byow.gui.services.WatchOnlyPasswordService;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.byow.wallet.byow.domains.node.ErrorMessages.WALLET_NAME_ALREADY_EXISTS;
import static java.time.ZoneOffset.UTC;
import static java.util.Date.from;
import static java.util.Objects.isNull;

@Component
public class ImportWatchOnlyWalletDialogController {
    private static final String DEFAULT_DATE = "04-01-2009";

    @FXML
    private PasswordField walletPassword;

    @FXML
    private DialogPane dialogPane;

    @FXML
    private TextField walletName;

    @FXML
    private TextArea extendedPubkeys;

    @FXML
    private DatePicker creationDate;

    @FXML
    private ButtonType ok;

    @FXML
    private ButtonType cancel;

    private final int initialNumberOfGeneratedAddresses;

    private final ValidateWalletService validateWalletService;

    private final AlertErrorService alertErrorService;

    private BooleanBinding allRequiredInputsAreFull;

    private final ConfigurableApplicationContext context;

    private final CreateWatchOnlyWalletService createWatchOnlyWalletService;

    private final WatchOnlyPasswordService watchOnlyPasswordService;

    private final Resource openQRCodeCaptureDialog;

    public ImportWatchOnlyWalletDialogController(
        @Qualifier("initialNumberOfGeneratedAddresses") int initialNumberOfGeneratedAddresses,
        ValidateWalletService validateWalletService,
        AlertErrorService alertErrorService,
        ConfigurableApplicationContext context,
        CreateWatchOnlyWalletService createWatchOnlyWalletService,
        WatchOnlyPasswordService watchOnlyPasswordService,
        @Value("fxml/qr_code_capture.fxml") Resource openQRCodeCaptureDialog
    ) {
        this.initialNumberOfGeneratedAddresses = initialNumberOfGeneratedAddresses;
        this.validateWalletService = validateWalletService;
        this.alertErrorService = alertErrorService;
        this.context = context;
        this.createWatchOnlyWalletService = createWatchOnlyWalletService;
        this.watchOnlyPasswordService = watchOnlyPasswordService;
        this.openQRCodeCaptureDialog = openQRCodeCaptureDialog;
    }

    public void initialize() {
        dialogPane.lookupButton(cancel)
            .addEventHandler(ActionEvent.ACTION, event -> dialogPane.getScene().getWindow().hide());
        allRequiredInputsAreFull = new BooleanBinding() {
            {
                bind(walletName.textProperty(), extendedPubkeys.textProperty());
            }

            @Override
            protected boolean computeValue() {
                return !(walletName.getText().trim().isEmpty() || extendedPubkeys.getText().trim().isEmpty());
            }
        };
        dialogPane.lookupButton(ok)
            .disableProperty()
            .bind(getAllRequiredInputsAreFull().not());
        dialogPane.lookupButton(ok)
            .addEventHandler(ActionEvent.ACTION, event -> importWallet());
    }

    private void importWallet() {
        if (validateWalletService.walletExists(walletName.getText())) {
            alertErrorService.alertError(WALLET_NAME_ALREADY_EXISTS);
            return;
        }
        Wallet wallet = createWatchOnlyWalletService.create(this.walletName.getText(), this.extendedPubkeys.getText(), buildDate(), initialNumberOfGeneratedAddresses);
        this.context.publishEvent(new ImportedWatchOnlyWalletEvent(this, wallet, watchOnlyPasswordService.encrypt(walletPassword.getText())));
        dialogPane.getScene().getWindow().hide();
    }

    private Date buildDate() {
        Date date = defaultDate();
        if (!isNull(creationDate.getValue())) {
            date = from(creationDate.getValue().atStartOfDay().toInstant(UTC));
        }
        return date;
    }

    private Date defaultDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        try {
            return simpleDateFormat.parse(DEFAULT_DATE);
        } catch (ParseException exception) {
            throw new RuntimeException(exception);
        }
    }

    public BooleanBinding getAllRequiredInputsAreFull() {
        return allRequiredInputsAreFull;
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
                        extendedPubkeys.setText(controller.getDecodedQRCode());
                    }
                });
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        dialog.show();
    }
}
