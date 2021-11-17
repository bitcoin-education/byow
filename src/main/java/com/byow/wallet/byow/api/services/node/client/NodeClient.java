package com.byow.wallet.byow.api.services.node.client;

import com.byow.wallet.byow.domains.node.NodeClientRequest;
import com.byow.wallet.byow.domains.node.NodeClientResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class NodeClient {

    private final RestTemplate restTemplate;

    public NodeClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public <T> T makeRequest(String method, ParameterizedTypeReference<NodeClientResponse<T>> responseType, String url, Object... params) {
        NodeClientRequest<Object> request = new NodeClientRequest<>(method, params);
        HttpEntity<NodeClientRequest<Object>> body = new HttpEntity<>(request);
        return restTemplate.exchange("/".concat(url), HttpMethod.POST, body, responseType).getBody().result();
    }
}
