package com.byow.wallet.byow.api.services.node.client;

import com.byow.wallet.byow.domains.node.NodeTransaction;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

import static java.lang.Integer.MAX_VALUE;

@Service
public class NodeListTransactionsClient {
    private final NodeClient nodeClient;

    public NodeListTransactionsClient(NodeClient nodeClient) {
        this.nodeClient = nodeClient;
    }

    public List<NodeTransaction> listTransactions(String wallet) {
        NodeTransaction[] transactionsArray = nodeClient.makeRequest("listtransactions", new ParameterizedTypeReference<>(){}, "wallet/".concat(wallet), "*", MAX_VALUE, 0, true);
        return Arrays.stream(transactionsArray).toList();
    }
}
