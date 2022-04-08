package com.byow.wallet.byow.domains.node;

public record NodeError(
    int code,
    String message
) {
}
