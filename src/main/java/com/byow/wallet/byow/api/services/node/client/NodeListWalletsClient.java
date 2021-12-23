package com.byow.wallet.byow.api.services.node.client;

import com.byow.wallet.byow.domains.node.NodeWallet;
import com.byow.wallet.byow.domains.node.NodeWallets;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NodeListWalletsClient {
    private final NodeClient nodeClient;

    public NodeListWalletsClient(NodeClient nodeClient) {
        this.nodeClient = nodeClient;
    }

    public List<String> listAll() {
        NodeWallets nodeWallets = nodeClient.makeRequest("listwalletdir", new ParameterizedTypeReference<>(){}, "");
        return nodeWallets.wallets()
            .stream()
            .map(NodeWallet::name)
            .toList();
    }

    public List<String> listLoaded() {
        return nodeClient.makeRequest("listwallets", new ParameterizedTypeReference<>() {}, "");
    }
}
