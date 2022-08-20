package com.byow.wallet.byow.gui.services;

import com.byow.wallet.byow.domains.node.NodeErrorWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import com.byow.wallet.byow.domains.node.Error;

import java.util.concurrent.Future;

@Service
public class NodeErrorHandler {
    private final ObjectMapper objectMapper;

    public NodeErrorHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public Future<Error> handleError(HttpServerErrorException.InternalServerError error) {
        try {
            NodeErrorWrapper nodeErrorWrapper = objectMapper.readValue(error.getResponseBodyAsString(), NodeErrorWrapper.class);
            return new AsyncResult<>(Error.from(nodeErrorWrapper.error()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
