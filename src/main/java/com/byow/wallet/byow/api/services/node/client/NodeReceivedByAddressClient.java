package com.byow.wallet.byow.api.services.node.client;

import com.byow.wallet.byow.domains.node.NodeAddress;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class NodeReceivedByAddressClient {
    private final NodeClient nodeClient;

    public NodeReceivedByAddressClient(NodeClient nodeClient) {
        this.nodeClient = nodeClient;
    }

    public List<NodeAddress> listAddresses(String walletName, int minConf, boolean includeEmpty, boolean includeWatchOnly) {
        NodeAddress[] nodeAddresses = nodeClient.makeRequest("listreceivedbyaddress", new ParameterizedTypeReference<>() {
        }, "wallet/".concat(walletName), minConf, includeEmpty, includeWatchOnly);
        return Arrays.stream(nodeAddresses)
            .toList();
    }
}
