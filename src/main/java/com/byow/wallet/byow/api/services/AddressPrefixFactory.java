package com.byow.wallet.byow.api.services;

import com.byow.wallet.byow.domains.AddressType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

import static com.byow.wallet.byow.domains.AddressType.SEGWIT;
import static io.github.bitcoineducation.bitcoinjava.AddressConstants.MAINNET_P2WPKH_ADDRESS_PREFIX;
import static io.github.bitcoineducation.bitcoinjava.AddressConstants.TESTNET_P2WPKH_ADDRESS_PREFIX;
import static io.github.bitcoineducation.bitcoinjava.AddressConstants.REGTEST_P2WPKH_ADDRESS_PREFIX;

@Service
public class AddressPrefixFactory {

    public static final String MAINNET = "mainnet";
    public static final String TESTNET = "testnet";
    public static final String REGTEST = "regtest";

    private final String bitcoinEnvironment;

    private final Map<AddressType, Map<String, String>> addressTypeMap = Map.of(
        SEGWIT, Map.of(
            MAINNET, MAINNET_P2WPKH_ADDRESS_PREFIX,
            TESTNET, TESTNET_P2WPKH_ADDRESS_PREFIX,
            REGTEST, REGTEST_P2WPKH_ADDRESS_PREFIX
        )
    );

    public AddressPrefixFactory(
        @Value("${bitcoinEnvironment}") String bitcoinEnvironment
    ) {
        this.bitcoinEnvironment = bitcoinEnvironment;
    }

    public String get(AddressType addressType) {
        return addressTypeMap.get(addressType).get(bitcoinEnvironment);
    }
}
