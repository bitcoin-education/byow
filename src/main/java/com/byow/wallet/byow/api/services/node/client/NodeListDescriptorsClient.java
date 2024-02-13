package com.byow.wallet.byow.api.services.node.client;

import com.byow.wallet.byow.domains.node.NodeDescriptor;
import com.byow.wallet.byow.domains.node.NodeListDescriptorsResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class NodeListDescriptorsClient {
    private final NodeClient nodeClient;

    public NodeListDescriptorsClient(NodeClient nodeClient) {
        this.nodeClient = nodeClient;
    }

    public Set<String> getDescriptors(String walletName) {
        NodeListDescriptorsResponse nodeListDescriptorsResponse = nodeClient.makeRequest("listdescriptors", new ParameterizedTypeReference<>() {}, "wallet/".concat(walletName));
        return nodeListDescriptorsResponse.descriptors().stream().map(NodeDescriptor::desc).collect(Collectors.toSet());
    }
}
