package com.byow.wallet.byow.api.services;

import com.byow.wallet.byow.domains.Address;
import com.byow.wallet.byow.domains.AddressConfig;
import io.github.bitcoineducation.bitcoinjava.ExtendedKey;
import io.github.bitcoineducation.bitcoinjava.ExtendedPubkey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.LongStream;

@Service
public class AddressSequentialGenerator {
    private final Map<String, AddressConfig> addressConfigs;

    private final Integer initialNumberOfGeneratedAddresses;

    public AddressSequentialGenerator(
        Map<String, AddressConfig> addressConfigs,
        @Qualifier("initialNumberOfGeneratedAddresses") Integer initialNumberOfGeneratedAddresses
    ) {
        this.addressConfigs = addressConfigs;
        this.initialNumberOfGeneratedAddresses = initialNumberOfGeneratedAddresses;
    }

    public List<Address> generate(String key, String type) {
        ExtendedPubkey extendedPubkey;
        try {
            extendedPubkey = ExtendedPubkey.unserialize(key);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return LongStream.range(0, initialNumberOfGeneratedAddresses)
            .mapToObj(i -> generateAddress(type, extendedPubkey, i))
            .toList();
    }

    private Address generateAddress(String type, ExtendedPubkey extendedPubkey, long index) {
        ExtendedKey extendedChildKey = extendedPubkey.ckd(String.valueOf(index));
        return new Address(
            addressConfigs.get(type).addressGenerator().generate(extendedChildKey),
            index
        );
    }

    @Autowired
    RestTemplate restTemplate;

    @PostConstruct
    public void exec() {
        String s = restTemplate.postForObject("/", Map.of(
            "method", "getblock",
            "params", List.of("0000000000000c5c0349ec731fe908af86a52cf80ea2bc45d9a4566c6c4a4014")
        ), String.class);
        System.out.println(s);
    }
}
