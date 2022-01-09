package com.byow.wallet.byow.domains.node;

public record NodeMultiImportAddressParams(
    NodeMultiImportAddressParamScriptPubKey scriptPubKey,
    long timestamp,
    boolean watchonly
) {
}
