package com.byow.wallet.byow.gui.listeners;

import com.byow.wallet.byow.gui.events.CreatedWalletEvent;
import com.byow.wallet.byow.gui.services.ImportWalletService;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.Future;

@Component
public class CreatedWalletImportListener implements ApplicationListener<CreatedWalletEvent> {
    private final ImportWalletService importWalletService;

    private Future<Void> result;

    public CreatedWalletImportListener(ImportWalletService importWalletService) {
        this.importWalletService = importWalletService;
    }

    @Override
    public void onApplicationEvent(CreatedWalletEvent event) {
        if (result != null) {
            result.cancel(true);
        }
        result = importWalletService.importWallet(event.getWallet());
    }
}
