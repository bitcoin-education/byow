package com.byow.wallet.byow.gui.events;

import com.byow.wallet.byow.domains.Wallet;
import com.byow.wallet.byow.gui.controllers.ImportWalletDialogController;
import org.springframework.context.ApplicationEvent;

public class ImportedWalletEvent extends ApplicationEvent {
    private final Wallet wallet;

    public ImportedWalletEvent(ImportWalletDialogController importWalletDialogController, Wallet wallet) {
        super(importWalletDialogController);
        this.wallet = wallet;
    }

    public Wallet getWallet() {
        return wallet;
    }
}
