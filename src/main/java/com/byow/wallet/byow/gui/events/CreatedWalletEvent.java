package com.byow.wallet.byow.gui.events;

import com.byow.wallet.byow.domains.Wallet;
import org.springframework.context.ApplicationEvent;

public class CreatedWalletEvent extends ApplicationEvent {
    private final Wallet wallet;

    public CreatedWalletEvent(Object source, Wallet wallet) {
        super(source);
        this.wallet = wallet;
    }

    public Wallet getWallet() {
        return wallet;
    }
}
