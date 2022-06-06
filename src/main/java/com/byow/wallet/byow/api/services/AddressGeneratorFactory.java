package com.byow.wallet.byow.api.services;

import com.byow.wallet.byow.domains.AddressType;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AddressGeneratorFactory {
    private final Map<String, AddressGenerator> addressGeneratorMap;

    public AddressGeneratorFactory(
        SegwitAddressGenerator segwitAddressGenerator,
        NestedSegwitAddressGenerator nestedSegwitAddressGenerator
    ) {
        this.addressGeneratorMap = Map.of(
            AddressType.SEGWIT.toString(), segwitAddressGenerator,
            AddressType.SEGWIT_CHANGE.toString(), segwitAddressGenerator,
            AddressType.NESTED_SEGWIT.toString(), nestedSegwitAddressGenerator,
            AddressType.NESTED_SEGWIT_CHANGE.toString(), nestedSegwitAddressGenerator
        );
    }

    public AddressGenerator get(String addressType) {
        return addressGeneratorMap.get(addressType);
    }
}
