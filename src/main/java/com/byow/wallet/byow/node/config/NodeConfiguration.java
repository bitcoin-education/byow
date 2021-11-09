package com.byow.wallet.byow.node.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ.Socket;

@Configuration
public class NodeConfiguration {
    @Bean
    public ZContext zContext() {
        return new ZContext();
    }

    @Bean
    public Socket subscriber(ZContext zContext) {
        return zContext.createSocket(SocketType.SUB);
    }
}