package com.byow.wallet.byow.api.services.node.client;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

@Service
public class NodeCreateWalletClient {
    private final NodeClient nodeClient;

    public NodeCreateWalletClient(NodeClient nodeClient) {
        this.nodeClient = nodeClient;
    }

    public void create(String name) {
        nodeClient.makeRequest("createwallet", new ParameterizedTypeReference<>() {}, "", name);
    }
}
