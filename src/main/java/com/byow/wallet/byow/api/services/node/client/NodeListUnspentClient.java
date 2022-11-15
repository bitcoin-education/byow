package com.byow.wallet.byow.api.services.node.client;

import com.byow.wallet.byow.domains.Utxo;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

import static java.lang.Integer.MAX_VALUE;

@Service
public class NodeListUnspentClient {
    private final NodeClient nodeClient;

    public NodeListUnspentClient(NodeClient nodeClient) {
        this.nodeClient = nodeClient;
    }

    public List<Utxo> listUnspent(List<String> addresses, String name) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Utxo[] utxosArray = nodeClient.makeRequest("listunspent", new ParameterizedTypeReference<>(){}, "wallet/".concat(name), 0, MAX_VALUE, addresses);
        return Arrays.stream(utxosArray).toList();
    }
}
