package com.byow.wallet.byow.api.services.node;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class NodeWalletCreateService {
    private final RestTemplate restTemplate;

    public NodeWalletCreateService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void create(String name) {
        restTemplate.postForEntity("/", Map.of(
                "method", "createwallet",
                "params", List.of(name)
            ), String.class
        );
    }
}
