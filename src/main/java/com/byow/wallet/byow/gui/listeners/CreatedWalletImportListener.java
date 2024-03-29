package com.byow.wallet.byow.gui.listeners;

import com.byow.wallet.byow.domains.Wallet;
import com.byow.wallet.byow.gui.events.CreatedWalletEvent;
import com.byow.wallet.byow.gui.events.ImportedWalletEvent;
import com.byow.wallet.byow.gui.events.ImportedWatchOnlyWalletEvent;
import com.byow.wallet.byow.gui.events.LoadedWalletEvent;
import com.byow.wallet.byow.gui.services.ImportWalletService;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.concurrent.Future;

@Component
public class CreatedWalletImportListener {
    private final ImportWalletService importWalletService;

    private Future<Void> result;

    public CreatedWalletImportListener(ImportWalletService importWalletService) {
        this.importWalletService = importWalletService;
    }

    @EventListener
    @Order(1)
    public void onCreatedWalletEvent(CreatedWalletEvent event) {
        importWallet(event.getWallet());
    }

    @EventListener
    @Order(0)
    public void onLoadedWalletEvent(LoadedWalletEvent event) {
        importWallet(event.getWallet());
    }

    @EventListener
    @Order(1)
    public void onImportedWalletEvent(ImportedWalletEvent event) {
        importWallet(event.getWallet());
    }

    @EventListener
    @Order(1)
    public void onImportedWatchOnlyWalletEvent(ImportedWatchOnlyWalletEvent event) {
        importWallet(event.getWallet());
    }

    public void importWallet(Wallet wallet) {
        if (result != null) {
            result.cancel(true);
        }
        result = importWalletService.importWallet(wallet);
    }
}
