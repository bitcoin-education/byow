package com.byow.wallet.byow.api.services.node.client;

import com.byow.wallet.byow.domains.node.NodeMultiImportAddressParamScriptPubKey;
import com.byow.wallet.byow.domains.node.NodeMultiImportAddressParams;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class NodeMultiImportAddressClient {
    private final NodeClient nodeClient;

    public NodeMultiImportAddressClient(NodeClient nodeClient) {
        this.nodeClient = nodeClient;
    }

    public void importAddresses(String walletName, List<String> addresses, Date walletCreatedAt) {
        List<NodeMultiImportAddressParams> params = addresses.stream()
            .map(address -> new NodeMultiImportAddressParams(
                new NodeMultiImportAddressParamScriptPubKey(address),
                walletCreatedAt.toInstant().getEpochSecond(),
                true
            )
        ).toList();
        nodeClient.makeRequest("importmulti", new ParameterizedTypeReference<>(){}, "wallet/".concat(walletName), params);
    }
}
