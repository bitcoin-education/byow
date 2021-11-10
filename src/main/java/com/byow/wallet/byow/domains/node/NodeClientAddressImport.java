package com.byow.wallet.byow.domains.node;

public record NodeClientAddressImport(
    NodeClientScriptPubkey scriptPubKey,
    long timestamp,
    boolean watchonly
) {
}
