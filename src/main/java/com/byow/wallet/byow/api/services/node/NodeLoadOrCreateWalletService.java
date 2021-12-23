package com.byow.wallet.byow.api.services.node;

import com.byow.wallet.byow.api.services.node.client.NodeCreateWalletClient;
import com.byow.wallet.byow.api.services.node.client.NodeListWalletsClient;
import com.byow.wallet.byow.api.services.node.client.NodeLoadWalletClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NodeLoadOrCreateWalletService {
    private final NodeCreateWalletClient nodeCreateWalletClient;

    private final NodeLoadWalletClient nodeLoadWalletClient;

    private final NodeListWalletsClient nodeListWalletsClient;

    public NodeLoadOrCreateWalletService(
        NodeCreateWalletClient nodeCreateWalletClient,
        NodeLoadWalletClient nodeLoadWalletClient,
        NodeListWalletsClient nodeListWalletsClient
    ) {
        this.nodeCreateWalletClient = nodeCreateWalletClient;
        this.nodeLoadWalletClient = nodeLoadWalletClient;
        this.nodeListWalletsClient = nodeListWalletsClient;
    }

    public void loadOrCreateWallet(String name) {
        List<String> allWallets = nodeListWalletsClient.listAll();
        List<String> loadedWallets = nodeListWalletsClient.listLoaded();
        if (loadedWallets.stream().anyMatch(nodeWallet -> nodeWallet.equals(name))) {
            return;
        }
        if (allWallets.stream().anyMatch(nodeWallet -> nodeWallet.equals(name))) {
            nodeLoadWalletClient.load(name);
            return;
        }
        nodeCreateWalletClient.create(name);
    }
}
