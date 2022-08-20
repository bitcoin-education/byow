package com.byow.wallet.byow.gui.events;

import com.byow.wallet.byow.domains.Wallet;
import com.byow.wallet.byow.gui.controllers.LoadWalletDialogController;
import org.springframework.context.ApplicationEvent;

public class LoadedWalletEvent extends ApplicationEvent {
    private final Wallet wallet;

    public LoadedWalletEvent(LoadWalletDialogController loadWalletDialogController, Wallet wallet) {
        super(loadWalletDialogController);
        this.wallet = wallet;
    }

    public Wallet getWallet() {
        return wallet;
    }
}
