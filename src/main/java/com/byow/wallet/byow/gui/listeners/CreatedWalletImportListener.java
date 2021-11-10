package com.byow.wallet.byow.gui.listeners;

import com.byow.wallet.byow.api.services.node.NodeImportWalletService;
import com.byow.wallet.byow.gui.events.CreatedWalletEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.Future;

@Component
public class CreatedWalletImportListener implements ApplicationListener<CreatedWalletEvent> {
    private final NodeImportWalletService nodeImportWalletService;

    private Future<Void> result;

    public CreatedWalletImportListener(NodeImportWalletService nodeImportWalletService) {
        this.nodeImportWalletService = nodeImportWalletService;
    }

    @Override
    public void onApplicationEvent(CreatedWalletEvent event) {
        if (result != null) {
            result.cancel(true);
        }
        result = nodeImportWalletService.load(event.getWallet());
    }

}
