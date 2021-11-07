package com.byow.wallet.byow.api.services;

import com.byow.wallet.byow.domains.Address;
import io.github.bitcoineducation.bitcoinjava.ExtendedKey;
import io.github.bitcoineducation.bitcoinjava.ExtendedPubkey;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.LongStream;

@Service
public class AddressSequentialGenerator {
    private final int initialNumberOfGeneratedAddresses;
    private final AddressGeneratorFactory addressGeneratorFactory;

    public AddressSequentialGenerator(
        @Qualifier("initialNumberOfGeneratedAddresses") int initialNumberOfGeneratedAddresses,
        AddressGeneratorFactory addressGeneratorFactory) {
        this.initialNumberOfGeneratedAddresses = initialNumberOfGeneratedAddresses;
        this.addressGeneratorFactory = addressGeneratorFactory;
    }

    public List<Address> generate(String key, String addressType) {
        ExtendedPubkey extendedPubkey;
        try {
            extendedPubkey = ExtendedPubkey.unserialize(key);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return LongStream.range(0, initialNumberOfGeneratedAddresses)
            .mapToObj(i -> generateAddress(addressGeneratorFactory.get(addressType), extendedPubkey, i))
            .toList();
    }

    private Address generateAddress(AddressGenerator addressGenerator, ExtendedPubkey extendedPubkey, long index) {
        ExtendedKey extendedChildKey = extendedPubkey.ckd(String.valueOf(index));
        return new Address(addressGenerator.generate(extendedChildKey), index);
    }
}
