package com.byow.wallet.byow.api.services.node;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class NodeWalletLoadService {
    private final RestTemplate restTemplate;

    public NodeWalletLoadService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void load(String name) {
        restTemplate.postForEntity("/", Map.of(
                "method", "loadwallet",
                "params", List.of(name)
            ), String.class
        );
    }
}
