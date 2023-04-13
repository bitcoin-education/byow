package com.byow.wallet.byow.domains;

import java.util.Date;
import java.util.List;

import static com.byow.wallet.byow.domains.AddressType.SEGWIT;

public record Wallet(String name, List<ExtendedPubkey> extendedPubkeys, Date createdAt, String mnemonicSeed) {
    public List<String> getAddresses() {
        return extendedPubkeys.stream()
            .flatMap(extendedPubkey -> extendedPubkey.getAddresses().stream())
            .map(Address::getAddress)
            .toList();
    }

    public String getFirstAddress() {
        return extendedPubkeys.stream()
            .filter(extendedPubkey -> extendedPubkey.getType().equals(SEGWIT.name()))
            .flatMap(extendedPubkey -> extendedPubkey.getAddresses().stream())
            .map(Address::getAddress)
            .findFirst()
            .orElseThrow();
    }

    public boolean isWatchOnly() {
        return mnemonicSeed.isBlank();
    }
}
