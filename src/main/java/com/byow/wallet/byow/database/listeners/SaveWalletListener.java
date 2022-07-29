package com.byow.wallet.byow.database.listeners;

import com.byow.wallet.byow.database.services.SaveWalletService;
import com.byow.wallet.byow.domains.Wallet;
import com.byow.wallet.byow.gui.events.CreatedWalletEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class SaveWalletListener implements ApplicationListener<CreatedWalletEvent> {
    private final SaveWalletService saveWalletService;

    public SaveWalletListener(SaveWalletService saveWalletService) {
        this.saveWalletService = saveWalletService;
    }

    @Override
    public void onApplicationEvent(CreatedWalletEvent event) {
        saveWallet(event.getWallet());
    }

    private void saveWallet(Wallet wallet) {
        saveWalletService.saveWallet(wallet);
    }
}
