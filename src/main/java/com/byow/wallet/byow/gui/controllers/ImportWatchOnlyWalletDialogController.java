package com.byow.wallet.byow.gui.controllers;

import com.byow.wallet.byow.api.services.CreateWatchOnlyWalletService;
import com.byow.wallet.byow.database.services.ValidateWalletService;
import com.byow.wallet.byow.domains.Wallet;
import com.byow.wallet.byow.gui.events.ImportedWatchOnlyWalletEvent;
import com.byow.wallet.byow.gui.services.AlertErrorService;
import com.byow.wallet.byow.gui.services.WatchOnlyPasswordService;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

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

    public ImportWatchOnlyWalletDialogController(
        @Qualifier("initialNumberOfGeneratedAddresses") int initialNumberOfGeneratedAddresses,
        ValidateWalletService validateWalletService,
        AlertErrorService alertErrorService,
        ConfigurableApplicationContext context,
        CreateWatchOnlyWalletService createWatchOnlyWalletService,
        WatchOnlyPasswordService watchOnlyPasswordService
    ) {
        this.initialNumberOfGeneratedAddresses = initialNumberOfGeneratedAddresses;
        this.validateWalletService = validateWalletService;
        this.alertErrorService = alertErrorService;
        this.context = context;
        this.createWatchOnlyWalletService = createWatchOnlyWalletService;
        this.watchOnlyPasswordService = watchOnlyPasswordService;
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
}
