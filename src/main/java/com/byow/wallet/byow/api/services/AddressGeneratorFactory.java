package com.byow.wallet.byow.api.services;

import org.springframework.stereotype.Service;

@Service
public class AddressGeneratorFactory {
    private final AddressConfigFinder addressConfigFinder;

    public AddressGeneratorFactory(AddressConfigFinder addressConfigFinder) {
        this.addressConfigFinder = addressConfigFinder;
    }

    public AddressGenerator get(String addressType) {
        return addressConfigFinder.findByAddressType(addressType).addressGenerator();
    }
}
