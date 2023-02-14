package com.byow.wallet.byow.gui.controllers;

import com.byow.wallet.byow.gui.services.ExtendedPubkeysGetter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextArea;
import org.springframework.stereotype.Component;

@Component
public class ExportExtendedPubkeysDialogController {
    @FXML
    private TextArea extendedPubkeys;

    @FXML
    private ButtonType ok;

    @FXML
    private DialogPane dialogPane;

    private final ExtendedPubkeysGetter extendedPubkeysGetter;

    public ExportExtendedPubkeysDialogController(ExtendedPubkeysGetter extendedPubkeysGetter) {
        this.extendedPubkeysGetter = extendedPubkeysGetter;
    }

    public void initialize() {
        exportExtendedPubkeys();
        dialogPane.lookupButton(ok)
            .addEventHandler(ActionEvent.ACTION, event -> dialogPane.getScene().getWindow().hide());
    }

    private void exportExtendedPubkeys() {
        extendedPubkeys.setText(extendedPubkeysGetter.get());
    }
}
