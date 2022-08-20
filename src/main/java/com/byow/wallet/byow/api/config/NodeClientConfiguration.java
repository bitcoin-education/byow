package com.byow.wallet.byow.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class NodeClientConfiguration {
    @Value("${node.rpc.uri}")
    private String nodeRpcUri;

    @Value("${node.rpc.username}")
    private String nodeRpcUsername;

    @Value("${node.rpc.password}")
    private String nodeRpcPassword;

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder.rootUri(nodeRpcUri)
            .basicAuthentication(nodeRpcUsername, nodeRpcPassword)
            .build();
    }
}
