package com.byow.wallet.byow.domains;

import java.math.BigDecimal;

public record Utxo(
    String txid,
    Long vout,
    String address,
    String label,
    String scriptPubKey,
    BigDecimal amount,
    Long confirmations,
    String redeemScript,
    String witnessScript
) {
}
