package com.byow.wallet.byow.gui.listeners;

import com.byow.wallet.byow.domains.Wallet;
import com.byow.wallet.byow.gui.events.ImportedWalletEvent;
import com.byow.wallet.byow.gui.events.LoadedWalletEvent;
import com.byow.wallet.byow.gui.services.UpdateCurrentWalletService;
import com.byow.wallet.byow.gui.services.UpdateUTXOsService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class LoadedWalletListener {
    private final UpdateCurrentWalletService updateCurrentWalletService;

    private final UpdateUTXOsService updateUTXOsService;

    public LoadedWalletListener(UpdateCurrentWalletService updateCurrentWalletService, UpdateUTXOsService updateUTXOsService) {
        this.updateCurrentWalletService = updateCurrentWalletService;
        this.updateUTXOsService = updateUTXOsService;
    }

    @EventListener
    public void onLoadedWalletEvent(LoadedWalletEvent event) {
        loadWallet(event.getWallet());
    }

    @EventListener
    public void onImportedWalletEvent(ImportedWalletEvent event) {
        loadWallet(event.getWallet());
    }

    public void loadWallet(Wallet wallet) {
        updateCurrentWalletService.update(wallet);
        updateUTXOsService.update(wallet.getAddresses(), wallet.getFirstAddress());
    }
}
