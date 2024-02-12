package com.byow.wallet.byow.domains.node;

public record NodeDescriptorInfoResponse(
    String descriptor,
    String checksum,
    boolean isrange,
    boolean issolvable,
    boolean hasprivatekeys
) {
}
