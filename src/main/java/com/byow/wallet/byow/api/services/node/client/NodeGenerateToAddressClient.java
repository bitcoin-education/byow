package com.byow.wallet.byow.api.services.node.client;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

@Service
public class NodeGenerateToAddressClient {
    private final NodeClient nodeClient;

    public NodeGenerateToAddressClient(NodeClient nodeClient) {
        this.nodeClient = nodeClient;
    }

    public void generateToAddress(String wallet, int blocks, String address) {
        nodeClient.makeRequest("generatetoaddress", new ParameterizedTypeReference<>() {}, "wallet/".concat(wallet), blocks, address);
    }
}
