package com.byow.wallet.byow.api.services.node.client;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

@Service
public class NodeGetNewAddressClient {
    private final NodeClient nodeClient;

    public NodeGetNewAddressClient(NodeClient nodeClient) {
        this.nodeClient = nodeClient;
    }

    public String getNewAddress(String wallet) {
        return nodeClient.makeRequest("getnewaddress", new ParameterizedTypeReference<>(){}, "wallet/".concat(wallet));
    }
}
