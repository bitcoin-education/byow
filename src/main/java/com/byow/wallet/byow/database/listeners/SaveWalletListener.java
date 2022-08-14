package com.byow.wallet.byow.database.listeners;

import com.byow.wallet.byow.database.services.SaveWalletService;
import com.byow.wallet.byow.domains.Wallet;
import com.byow.wallet.byow.gui.events.CreatedWalletEvent;
import com.byow.wallet.byow.gui.events.ImportedWalletEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
public class SaveWalletListener {
    private final SaveWalletService saveWalletService;

    public SaveWalletListener(SaveWalletService saveWalletService) {
        this.saveWalletService = saveWalletService;
    }

    @EventListener
    @Order(0)
    public void onCreatedWallet(CreatedWalletEvent event) {
        saveWallet(event.getWallet());
    }

    @EventListener
    @Order(0)
    public void onImportedWalletEvent(ImportedWalletEvent event) {
        saveWallet(event.getWallet());
    }

    private void saveWallet(Wallet wallet) {
        saveWalletService.saveWallet(wallet);
    }
}
