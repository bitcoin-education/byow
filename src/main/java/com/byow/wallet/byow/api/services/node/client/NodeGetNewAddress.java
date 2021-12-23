package com.byow.wallet.byow.api.services.node.client;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

@Service
public class NodeGetNewAddress {
    private final NodeClient nodeClient;

    public NodeGetNewAddress(NodeClient nodeClient) {
        this.nodeClient = nodeClient;
    }

    public String getNewAddress(String wallet) {
        return nodeClient.makeRequest("getnewaddress", new ParameterizedTypeReference<>(){}, "wallet/".concat(wallet));
    }
}
