package com.byow.wallet.byow.api.services;

import com.byow.wallet.byow.domains.AddressType;
import com.byow.wallet.byow.domains.Environment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AddressPrefixFactory {
    private final String bitcoinEnvironment;

    private final AddressConfigFinder addressConfigFinder;

    public AddressPrefixFactory(
        @Value("${bitcoinEnvironment}") String bitcoinEnvironment,
        AddressConfigFinder addressConfigFinder
    ) {
        this.bitcoinEnvironment = bitcoinEnvironment;
        this.addressConfigFinder = addressConfigFinder;
    }

    public String get(AddressType addressType) {
        return addressConfigFinder.findByAddressType(addressType.toString())
            .addressPrefixes()
            .get(Environment.valueOf(bitcoinEnvironment.toUpperCase()));
    }
}
