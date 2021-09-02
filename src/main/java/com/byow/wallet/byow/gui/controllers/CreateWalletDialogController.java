package com.byow.wallet.byow.gui.controllers;

import com.byow.wallet.byow.api.services.MnemonicSeedService;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;

@Component
public class CreateWalletDialogController {
    @FXML
    private TextArea mnemonicSeed;

    private final MnemonicSeedService mnemonicSeedService;

    public CreateWalletDialogController(MnemonicSeedService mnemonicSeedService) {
        this.mnemonicSeedService = mnemonicSeedService;
    }

    public void createMnemonicSeed() throws FileNotFoundException {
        mnemonicSeed.setText(mnemonicSeedService.create());
    }
}
