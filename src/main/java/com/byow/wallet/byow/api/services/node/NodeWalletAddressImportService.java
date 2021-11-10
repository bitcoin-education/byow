package com.byow.wallet.byow.api.services.node;

import com.byow.wallet.byow.domains.node.NodeClientAddressImport;
import com.byow.wallet.byow.domains.node.NodeClientRequest;
import com.byow.wallet.byow.domains.node.NodeClientScriptPubkey;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.List;

@Service
public class NodeWalletAddressImportService {
    private final RestTemplate restTemplate;

    public NodeWalletAddressImportService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void importAddresses(String walletName, List<String> addresses, Date walletCreatedAt) {
        List<NodeClientAddressImport> params = addresses.stream()
            .map(address -> new NodeClientAddressImport(
                    new NodeClientScriptPubkey(address),
                    walletCreatedAt.toInstant().getEpochSecond(),
                    true
                )
            ).toList();
        NodeClientRequest<List<List<NodeClientAddressImport>>> importMultiBody = new NodeClientRequest<>("importmulti", List.of(params));
        HttpEntity<NodeClientRequest<List<List<NodeClientAddressImport>>>> entity = new HttpEntity<>(importMultiBody);
        restTemplate.exchange("/wallet/".concat(walletName), HttpMethod.POST, entity, String.class);
    }
}
