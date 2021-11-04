package com.byow.wallet.byow.api.services;

import com.byow.wallet.byow.domains.AddressType;

import java.util.Map;

public class AddressGeneratorFactory {
    private final Map<String, AddressGenerator> addressGeneratorMap;

    public AddressGeneratorFactory(SegwitAddressGenerator segwitAddressGenerator) {
        this.addressGeneratorMap = Map.of(
            AddressType.SEGWIT.toString(), segwitAddressGenerator,
            AddressType.SEGWIT_CHANGE.toString(), segwitAddressGenerator
        );
    }

    public AddressGenerator get(String addressType) {
        return addressGeneratorMap.get(addressType);
    }
}
