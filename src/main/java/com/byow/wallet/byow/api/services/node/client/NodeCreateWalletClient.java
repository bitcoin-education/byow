package com.byow.wallet.byow.api.services.node.client;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

@Service
public class NodeCreateWalletClient {
    private final NodeClient nodeClient;

    public NodeCreateWalletClient(NodeClient nodeClient) {
        this.nodeClient = nodeClient;
    }

    public void create(String name, boolean disablePrivateKeys, boolean blank, String passphrase, boolean avoidReuse, boolean descriptors) {
        nodeClient.makeRequest("createwallet", new ParameterizedTypeReference<>(){}, "", name, disablePrivateKeys, blank, passphrase, avoidReuse, descriptors);
    }
}
