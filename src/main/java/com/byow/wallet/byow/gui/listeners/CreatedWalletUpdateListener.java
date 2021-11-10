package com.byow.wallet.byow.gui.listeners;

import com.byow.wallet.byow.gui.events.CreatedWalletEvent;
import com.byow.wallet.byow.gui.services.UpdateCurrentWalletService;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class CreatedWalletUpdateListener implements ApplicationListener<CreatedWalletEvent> {
    private final UpdateCurrentWalletService updateCurrentWalletService;

    public CreatedWalletUpdateListener(UpdateCurrentWalletService updateCurrentWalletService) {
        this.updateCurrentWalletService = updateCurrentWalletService;
    }

    @Override
    public void onApplicationEvent(CreatedWalletEvent event) {
        updateCurrentWalletService.update(event.getWallet());
    }
}
