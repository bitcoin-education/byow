package com.byow.wallet.byow.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class AsyncConfig {
    @Bean
    public ExecutorService defaultExecutorService() {
        return Executors.newSingleThreadExecutor();
    }

    @Bean
    public ExecutorService nodeExecutorService() {
        return Executors.newSingleThreadExecutor();
    }
}
