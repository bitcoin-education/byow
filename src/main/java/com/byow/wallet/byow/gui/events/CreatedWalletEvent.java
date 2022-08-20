package com.byow.wallet.byow.gui.events;

import com.byow.wallet.byow.domains.Wallet;
import com.byow.wallet.byow.gui.controllers.CreateWalletDialogController;
import org.springframework.context.ApplicationEvent;

public class CreatedWalletEvent extends ApplicationEvent {
    private final Wallet wallet;

    public CreatedWalletEvent(CreateWalletDialogController createWalletDialogController, Wallet wallet) {
        super(createWalletDialogController);
        this.wallet = wallet;
    }

    public Wallet getWallet() {
        return wallet;
    }
}
