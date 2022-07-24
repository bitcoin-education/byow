package com.byow.wallet.byow.api.services;

import com.byow.wallet.byow.domains.ExtendedPubkey;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AddAddressService {
    private final AddressSequentialGenerator addressSequentialGenerator;

    public AddAddressService(AddressSequentialGenerator addressSequentialGenerator) {
        this.addressSequentialGenerator = addressSequentialGenerator;
    }

    public void addAddresses(List<ExtendedPubkey> extendedPubkeys, long fromIndex, int numberOfGeneratedAddresses) {
        extendedPubkeys.forEach(extendedPubkey -> addAddresses(extendedPubkey, fromIndex, numberOfGeneratedAddresses));
    }

    private void addAddresses(ExtendedPubkey extendedPubkey, long fromIndex, int numberOfGeneratedAddresses) {
        extendedPubkey.addAddresses(new ArrayList<>(addressSequentialGenerator.generate(extendedPubkey.getKey(), extendedPubkey.getType(), fromIndex, numberOfGeneratedAddresses)));
    }
}
