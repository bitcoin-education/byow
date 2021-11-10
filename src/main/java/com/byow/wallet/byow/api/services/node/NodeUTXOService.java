package com.byow.wallet.byow.api.services.node;

import com.byow.wallet.byow.domains.node.NodeClientRequest;
import com.byow.wallet.byow.domains.node.NodeClientResponse;
import com.byow.wallet.byow.domains.Utxo;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class NodeUTXOService {
    private final RestTemplate restTemplate;

    public NodeUTXOService(
        RestTemplate restTemplate
    ) {
        this.restTemplate = restTemplate;
    }

    public List<Utxo> getUtxos(List<String> addresses, String name) {
        return listUnspent(addresses, name);
    }

    private List<Utxo> listUnspent(List<String> addresses, String name) {
        NodeClientRequest<List<Object>> listunspentBody = new NodeClientRequest<>("listunspent", List.of(0, Integer.MAX_VALUE, addresses));
        HttpEntity<NodeClientRequest<List<Object>>> entity = new HttpEntity<>(listunspentBody);
        Utxo[] utxosArray = restTemplate.exchange("/wallet/".concat(name), HttpMethod.POST, entity, new ParameterizedTypeReference<NodeClientResponse<Utxo[]>>() {})
            .getBody()
            .result();

        if (utxosArray != null) {
            return Arrays.stream(utxosArray).toList();
        }

        return List.of();
    }
}
