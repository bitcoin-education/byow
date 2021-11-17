package com.byow.wallet.byow.api.services.node.client;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

@Service
public class NodeLoadWalletClient {
    private final NodeClient nodeClient;

    public NodeLoadWalletClient(NodeClient nodeClient) {
        this.nodeClient = nodeClient;
    }

    public void load(String name) {
        nodeClient.makeRequest("loadwallet", new ParameterizedTypeReference<>() {}, "", name);
    }
}
