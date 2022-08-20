package com.byow.wallet.byow.node.listeners;

import com.byow.wallet.byow.gui.services.UpdateUTXOsService;
import com.byow.wallet.byow.node.events.BlockReceivedEvent;
import com.byow.wallet.byow.observables.CurrentWallet;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BlockReceivedListener implements ApplicationListener<BlockReceivedEvent> {
    private final CurrentWallet currentWallet;

    private final UpdateUTXOsService updateUTXOsService;

    public BlockReceivedListener(CurrentWallet currentWallet, UpdateUTXOsService updateUTXOsService) {
        this.currentWallet = currentWallet;
        this.updateUTXOsService = updateUTXOsService;
    }

    @Override
    public void onApplicationEvent(BlockReceivedEvent event) {
        List<String> addresses = currentWallet.getAddressesAsStrings();
        if (!addresses.isEmpty()) {
            updateUTXOsService.update(addresses, currentWallet.getFirstAddress());
        }
    }
}
