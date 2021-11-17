package com.byow.wallet.byow.domains.node;

public record NodeClientRequest<T>(
    String method,
    T params
) {
}
