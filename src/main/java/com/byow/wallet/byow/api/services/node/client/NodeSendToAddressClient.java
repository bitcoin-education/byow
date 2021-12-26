package com.byow.wallet.byow.api.services.node.client;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

@Service
public class NodeSendToAddressClient {
    private final NodeClient nodeClient;

    public NodeSendToAddressClient(NodeClient nodeClient) {
        this.nodeClient = nodeClient;
    }

    public void sendToAddress(String wallet, String address, double amount) {
        nodeClient.makeRequest("sendtoaddress", new ParameterizedTypeReference<>(){}, "wallet/".concat(wallet), address, amount);
    }
}
