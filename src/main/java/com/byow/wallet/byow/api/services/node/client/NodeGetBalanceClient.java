package com.byow.wallet.byow.api.services.node.client;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

@Service
public class NodeGetBalanceClient {
    private final NodeClient nodeClient;

    public NodeGetBalanceClient(NodeClient nodeClient) {
        this.nodeClient = nodeClient;
    }

    public Double get(String wallet) {
        return nodeClient.makeRequest("getbalance", new ParameterizedTypeReference<>(){}, "wallet/".concat(wallet));
    }
}
