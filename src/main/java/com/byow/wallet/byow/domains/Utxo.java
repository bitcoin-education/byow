package com.byow.wallet.byow.domains;

public record Utxo(
    String txid,
    Long vout,
    String address,
    String label,
    String scriptPubKey,
    Double amount,
    Long confirmations,
    String redeemScript,
    String witnessScript
) {}
