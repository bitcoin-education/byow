package com.byow.wallet.byow.gui.events;

import com.byow.wallet.byow.domains.Wallet;
import com.byow.wallet.byow.gui.controllers.ImportWatchOnlyWalletDialogController;
import org.springframework.context.ApplicationEvent;

public class ImportedWatchOnlyWalletEvent extends ApplicationEvent {
    private final Wallet wallet;

    private final String password;

    public ImportedWatchOnlyWalletEvent(ImportWatchOnlyWalletDialogController importWatchOnlyWalletDialogController, Wallet wallet, String password) {
        super(importWatchOnlyWalletDialogController);
        this.wallet = wallet;
        this.password = password;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public String getPassword() {
        return password;
    }
}
