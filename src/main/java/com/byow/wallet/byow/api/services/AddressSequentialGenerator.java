package com.byow.wallet.byow.api.services;

import com.byow.wallet.byow.domains.Address;
import com.byow.wallet.byow.domains.AddressType;
import io.github.bitcoineducation.bitcoinjava.ExtendedKey;
import io.github.bitcoineducation.bitcoinjava.ExtendedPubkey;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.LongStream;

@Service
public class AddressSequentialGenerator {
    private final AddressGeneratorFactory addressGeneratorFactory;

    private final AddressPrefixFactory addressPrefixFactory;

    public AddressSequentialGenerator(
        AddressGeneratorFactory addressGeneratorFactory,
        AddressPrefixFactory addressPrefixFactory
    ) {
        this.addressGeneratorFactory = addressGeneratorFactory;
        this.addressPrefixFactory = addressPrefixFactory;
    }

    public List<Address> generate(String key, String addressType, long fromIndex, int numberOfGeneratedAddresses) {
        ExtendedPubkey extendedPubkey;
        try {
            extendedPubkey = ExtendedPubkey.unserialize(key);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return LongStream.range(fromIndex, fromIndex + numberOfGeneratedAddresses)
            .mapToObj(i -> generateAddress(addressGeneratorFactory.get(addressType), extendedPubkey, i, AddressType.valueOf(addressType)))
            .toList();
    }

    private Address generateAddress(AddressGenerator addressGenerator, ExtendedPubkey extendedPubkey, long index, AddressType addressType) {
        ExtendedKey extendedChildKey = extendedPubkey.ckd(String.valueOf(index));
        return new Address(addressGenerator.generate(extendedChildKey, addressPrefixFactory.get(addressType)), index, BigDecimal.ZERO, 0, addressType);
    }
}
