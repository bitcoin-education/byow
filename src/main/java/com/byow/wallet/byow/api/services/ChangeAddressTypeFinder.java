package com.byow.wallet.byow.api.services;

import com.byow.wallet.byow.domains.AddressType;
import org.springframework.stereotype.Service;

import static com.byow.wallet.byow.domains.AddressType.SEGWIT;

@Service
public class ChangeAddressTypeFinder {
    private final AddressConfigFinder addressConfigFinder;

    public ChangeAddressTypeFinder(AddressConfigFinder addressConfigFinder) {
        this.addressConfigFinder = addressConfigFinder;
    }

    public AddressType find(String address) {
        return addressConfigFinder.findByAddress(address)
            .map(addressConfig -> AddressType.valueOf(addressConfig.addressType().toString().concat("_CHANGE")))
            .orElse(SEGWIT);
    }
}
