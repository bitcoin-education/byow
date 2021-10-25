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
    private final Integer initialNumberOfGeneratedAddresses;

    public AddressSequentialGenerator(
        @Qualifier("initialNumberOfGeneratedAddresses") Integer initialNumberOfGeneratedAddresses
    ) {
        this.initialNumberOfGeneratedAddresses = initialNumberOfGeneratedAddresses;
    }

    public List<Address> generate(String key, AddressGenerator addressGenerator) {
        ExtendedPubkey extendedPubkey;
        try {
            extendedPubkey = ExtendedPubkey.unserialize(key);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return LongStream.range(0, initialNumberOfGeneratedAddresses)
            .mapToObj(i -> generateAddress(addressGenerator, extendedPubkey, i))
            .toList();
    }

    private Address generateAddress(AddressGenerator addressGenerator, ExtendedPubkey extendedPubkey, long index) {
        ExtendedKey extendedChildKey = extendedPubkey.ckd(String.valueOf(index));
        return new Address(addressGenerator.generate(extendedChildKey), index);
    }
}
