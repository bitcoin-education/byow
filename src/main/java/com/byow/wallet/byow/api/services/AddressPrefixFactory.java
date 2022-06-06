package com.byow.wallet.byow.api.services;

import com.byow.wallet.byow.domains.AddressType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

import static com.byow.wallet.byow.domains.AddressType.NESTED_SEGWIT;
import static com.byow.wallet.byow.domains.AddressType.SEGWIT;
import static io.github.bitcoineducation.bitcoinjava.AddressConstants.*;

@Service
public class AddressPrefixFactory {
    public static final String MAINNET = "mainnet";
    public static final String TESTNET = "testnet";
    public static final String REGTEST = "regtest";

    private final Map<AddressType, Map<String, String>> addressTypeMap = Map.of(
        SEGWIT, Map.of(
            MAINNET, MAINNET_P2WPKH_ADDRESS_PREFIX,
            TESTNET, TESTNET_P2WPKH_ADDRESS_PREFIX,
            REGTEST, REGTEST_P2WPKH_ADDRESS_PREFIX
        ),
        NESTED_SEGWIT, Map.of(
            MAINNET, MAINNET_P2SH_ADDRESS_PREFIX,
            TESTNET, TESTNET_P2SH_ADDRESS_PREFIX,
            REGTEST, TESTNET_P2SH_ADDRESS_PREFIX
        )
    );

    private final String bitcoinEnvironment;

    public AddressPrefixFactory(
        @Value("${bitcoinEnvironment}") String bitcoinEnvironment
    ) {
        this.bitcoinEnvironment = bitcoinEnvironment;
    }

    public String get(AddressType addressType) {
        return addressTypeMap.get(addressType).get(bitcoinEnvironment);
    }
}
