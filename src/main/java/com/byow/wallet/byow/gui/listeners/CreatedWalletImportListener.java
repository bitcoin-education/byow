package com.byow.wallet.byow.gui.listeners;

import com.byow.wallet.byow.api.services.node.NodeLoadOrCreateWalletService;
import com.byow.wallet.byow.api.services.node.client.NodeMultiImportAddressClient;
import com.byow.wallet.byow.domains.Wallet;
import com.byow.wallet.byow.gui.events.CreatedWalletEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class CreatedWalletImportListener implements ApplicationListener<CreatedWalletEvent> {
    private final NodeLoadOrCreateWalletService nodeLoadOrCreateWalletService;

    private final NodeMultiImportAddressClient nodeMultiImportAddressClient;

    public CreatedWalletImportListener(NodeLoadOrCreateWalletService nodeLoadOrCreateWalletService, NodeMultiImportAddressClient nodeMultiImportAddressClient) {
        this.nodeLoadOrCreateWalletService = nodeLoadOrCreateWalletService;
        this.nodeMultiImportAddressClient = nodeMultiImportAddressClient;
    }

    @Override
    public void onApplicationEvent(CreatedWalletEvent event) {
        Wallet wallet = event.getWallet();
        nodeLoadOrCreateWalletService.loadOrCreateWallet(wallet.name());
        nodeMultiImportAddressClient.importAddresses(wallet.name(), wallet.getAddresses(), wallet.createdAt());
    }
}
