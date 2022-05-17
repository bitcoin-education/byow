package com.byow.wallet.byow.api.services.node.client;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

@Service
public class NodeSendRawTransactionClient {
    private final NodeClient nodeClient;

    public NodeSendRawTransactionClient(NodeClient nodeClient) {
        this.nodeClient = nodeClient;
    }

    public void send(String transactionHex) {
        nodeClient.makeRequest("sendrawtransaction", new ParameterizedTypeReference<>(){}, "", transactionHex);
    }
}
