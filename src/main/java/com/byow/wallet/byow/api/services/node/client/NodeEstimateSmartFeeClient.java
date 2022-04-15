package com.byow.wallet.byow.api.services.node.client;

import com.byow.wallet.byow.domains.node.NodeFee;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

@Service
public class NodeEstimateSmartFeeClient {
    private final NodeClient nodeClient;

    public NodeEstimateSmartFeeClient(NodeClient nodeClient) {
        this.nodeClient = nodeClient;
    }

    public NodeFee estimateSmartFee(Integer blocks) {
        return nodeClient.makeRequest("estimatesmartfee", new ParameterizedTypeReference<>(){}, "", blocks);
    }
}
