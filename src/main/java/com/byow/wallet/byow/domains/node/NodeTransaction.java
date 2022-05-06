package com.byow.wallet.byow.domains.node;

import java.math.BigDecimal;
import java.time.Instant;

public record NodeTransaction(
    String txid,
    Long confirmations,
    BigDecimal amount,
    String address,
    Instant time
) {
}