package com.byow.wallet.byow.database.listeners;

import com.byow.wallet.byow.database.services.SaveWalletService;
import com.byow.wallet.byow.database.services.SaveWatchOnlyWalletService;
import com.byow.wallet.byow.domains.Wallet;
import com.byow.wallet.byow.gui.events.CreatedWalletEvent;
import com.byow.wallet.byow.gui.events.ImportedWalletEvent;
import com.byow.wallet.byow.gui.events.ImportedWatchOnlyWalletEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
public class SaveWalletListener {
    private final SaveWalletService saveWalletService;

    private final SaveWatchOnlyWalletService saveWatchOnlyWalletService;

    public SaveWalletListener(SaveWalletService saveWalletService, SaveWatchOnlyWalletService saveWatchOnlyWalletService) {
        this.saveWalletService = saveWalletService;
        this.saveWatchOnlyWalletService = saveWatchOnlyWalletService;
    }

    @EventListener
    @Order(0)
    public void onCreatedWalletEvent(CreatedWalletEvent event) {
        saveWallet(event.getWallet());
    }

    @EventListener
    @Order(0)
    public void onImportedWalletEvent(ImportedWalletEvent event) {
        saveWallet(event.getWallet());
    }

    @EventListener
    @Order(0)
    public void onImportedWatchOnlyWalletEvent(ImportedWatchOnlyWalletEvent event) {
        saveWatchOnlyWallet(event.getWallet(), event.getPassword());
    }

    private void saveWatchOnlyWallet(Wallet wallet, String password) {
        saveWatchOnlyWalletService.saveWallet(wallet, password);
    }

    private void saveWallet(Wallet wallet) {
        saveWalletService.saveWallet(wallet);
    }
}
