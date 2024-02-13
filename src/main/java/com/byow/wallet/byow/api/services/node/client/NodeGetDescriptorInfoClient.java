package com.byow.wallet.byow.api.services.node.client;

import com.byow.wallet.byow.domains.node.NodeDescriptorInfoResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NodeGetDescriptorInfoClient {
    private final NodeClient nodeClient;

    public NodeGetDescriptorInfoClient(NodeClient nodeClient) {
        this.nodeClient = nodeClient;
    }

    public List<NodeDescriptorInfoResponse> getDescriptorsInfo(String walletName, List<String> descriptors) {
        return descriptors.stream().map(descriptor ->
                nodeClient.<NodeDescriptorInfoResponse>makeRequest("getdescriptorinfo", new ParameterizedTypeReference<>(){}, "wallet/".concat(walletName), descriptor)
            ).toList();
    }
}
