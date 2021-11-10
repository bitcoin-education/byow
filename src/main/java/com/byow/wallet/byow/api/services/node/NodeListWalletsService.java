package com.byow.wallet.byow.api.services.node;

import com.byow.wallet.byow.domains.node.NodeClientRequest;
import com.byow.wallet.byow.domains.node.NodeClientResponse;
import com.byow.wallet.byow.domains.node.NodeWallets;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class NodeListWalletsService {
    private final RestTemplate restTemplate;

    public NodeListWalletsService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public NodeWallets listAll() {
        NodeClientRequest<Object> request = new NodeClientRequest<>("listwalletdir", List.of());
        HttpEntity<NodeClientRequest<Object>> body = new HttpEntity<>(request);
        return restTemplate.exchange("/", HttpMethod.POST, body, new ParameterizedTypeReference<NodeClientResponse<NodeWallets>>(){}).getBody().result();
    }

    public List<String> listLoaded() {
        NodeClientRequest<Object> request = new NodeClientRequest<>("listwallets", List.of());
        HttpEntity<NodeClientRequest<Object>> body = new HttpEntity<>(request);
        return restTemplate.exchange("/", HttpMethod.POST, body, new ParameterizedTypeReference<NodeClientResponse<List<String>>>(){}).getBody().result();
    }
}
