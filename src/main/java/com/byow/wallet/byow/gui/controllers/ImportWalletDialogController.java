package com.byow.wallet.byow.gui.controllers;

import com.byow.wallet.byow.api.services.CreateWalletService;
import com.byow.wallet.byow.domains.Wallet;
import com.byow.wallet.byow.gui.events.CreatedWalletEvent;
import com.byow.wallet.byow.gui.events.ImportedWalletEvent;
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

import static java.time.ZoneOffset.UTC;
import static java.util.Date.from;
import static java.util.Objects.isNull;

@Component
public class ImportWalletDialogController {
    private static final String DEFAULT_DATE = "04-01-2009";
    @FXML
    private DialogPane dialogPane;

    @FXML
    private TextField walletName;

    @FXML
    private TextArea mnemonicSeed;

    @FXML
    private PasswordField walletPassword;

    @FXML
    private DatePicker creationDate;

    @FXML
    private ButtonType ok;

    @FXML
    private ButtonType cancel;

    private BooleanBinding allRequiredInputsAreFull;

    private final CreateWalletService createWalletService;

    private final int initialNumberOfGeneratedAddresses;

    private final ConfigurableApplicationContext context;

    public ImportWalletDialogController(
        CreateWalletService createWalletService,
        @Qualifier("initialNumberOfGeneratedAddresses") int initialNumberOfGeneratedAddresses,
        ConfigurableApplicationContext context
    ) {
        this.createWalletService = createWalletService;
        this.initialNumberOfGeneratedAddresses = initialNumberOfGeneratedAddresses;
        this.context = context;
    }

    public void initialize() {
        dialogPane.lookupButton(cancel)
            .addEventHandler(ActionEvent.ACTION, event -> dialogPane.getScene().getWindow().hide());
        allRequiredInputsAreFull = new BooleanBinding() {
            {
                bind(walletName.textProperty(), mnemonicSeed.textProperty());
            }

            @Override
            protected boolean computeValue() {
                return !(walletName.getText().trim().isEmpty() || mnemonicSeed.getText().trim().isEmpty());
            }
        };
        dialogPane.lookupButton(ok)
            .disableProperty()
            .bind(getAllRequiredInputsAreFull().not());
        dialogPane.lookupButton(ok)
            .addEventHandler(ActionEvent.ACTION, event -> importWallet());
    }

    private void importWallet() {
        Wallet wallet = createWalletService.create(this.walletName.getText(), this.walletPassword.getText(), this.mnemonicSeed.getText(), buildDate(), initialNumberOfGeneratedAddresses);
        this.context.publishEvent(new ImportedWalletEvent(this, wallet));
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