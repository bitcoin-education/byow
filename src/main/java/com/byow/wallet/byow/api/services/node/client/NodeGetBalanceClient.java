package com.byow.wallet.byow.api.services.node.client;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class NodeGetBalanceClient {
    private final NodeClient nodeClient;

    public NodeGetBalanceClient(NodeClient nodeClient) {
        this.nodeClient = nodeClient;
    }

    @PostConstruct
    public void exec() {
        Double balance = get("testing wallet name");
        System.out.println(balance);
    }

    public Double get(String wallet) {
        return nodeClient.makeRequest("getbalance", new ParameterizedTypeReference<>(){}, "wallet/".concat(wallet));
    }
}
