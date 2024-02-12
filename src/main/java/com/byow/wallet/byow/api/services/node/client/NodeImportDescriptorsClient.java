package com.byow.wallet.byow.api.services.node.client;

import com.byow.wallet.byow.domains.node.NodeImportDescriptorsParams;
import com.byow.wallet.byow.domains.node.NodeImportDescriptorsResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class NodeImportDescriptorsClient {
    private final NodeClient nodeClient;

    private final int initialNumberOfGeneratedAddresses;

    public NodeImportDescriptorsClient(
        NodeClient nodeClient,
        @Qualifier("initialNumberOfGeneratedAddresses") int initialNumberOfGeneratedAddresses
    ) {
        this.nodeClient = nodeClient;
        this.initialNumberOfGeneratedAddresses = initialNumberOfGeneratedAddresses;
    }

    public void importDescriptors(String walletName, List<String> descriptors, Date walletCreatedAt) {
        List<NodeImportDescriptorsParams> params = descriptors.stream()
                .map(descriptor ->
                    new NodeImportDescriptorsParams(
                        descriptor,
                       true,
                        List.of(0, initialNumberOfGeneratedAddresses),
                        0,
                        walletCreatedAt.toInstant().getEpochSecond()
                    )
                ).toList();
        NodeImportDescriptorsResponse[] result = nodeClient.makeRequest("importdescriptors", new ParameterizedTypeReference<>(){}, "wallet/".concat(walletName), params);
    }
}
