package com.byow.wallet.byow.domains.node;

public record NodeClientResponse<T>(
    T result
) {
}
