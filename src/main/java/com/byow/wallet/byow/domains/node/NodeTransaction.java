package com.byow.wallet.byow.domains.node;

import java.math.BigDecimal;
import java.time.Instant;

import static java.util.Objects.isNull;

public record NodeTransaction(
    String txid,
    Long confirmations,
    BigDecimal amount,
    BigDecimal fee,
    String address,
    Instant time
) {
    public BigDecimal totalSpent() {
        if (isNull(fee)) {
            return amount;
        }
        return amount.add(fee);
    }
}
