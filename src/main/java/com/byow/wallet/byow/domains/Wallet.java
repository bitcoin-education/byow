package com.byow.wallet.byow.domains;

import java.util.Date;
import java.util.List;

public record Wallet(String name, List<ExtendedPubkey> extendedPubkeys, Date createdAt) {
    public List<String> getAddresses() {
        return extendedPubkeys.stream()
            .flatMap(extendedPubkey -> extendedPubkey.getAddresses().stream())
            .map(Address::address)
            .toList();
    }
}
