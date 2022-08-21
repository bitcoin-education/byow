package com.byow.wallet.byow.api.config;

import com.byow.wallet.byow.api.services.*;
import com.byow.wallet.byow.domains.AddressConfig;
import com.byow.wallet.byow.domains.Environment;
import io.github.bitcoineducation.bitcoinjava.Script;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.byow.wallet.byow.domains.AddressType.*;
import static com.byow.wallet.byow.domains.Environment.*;
import static com.byow.wallet.byow.utils.AddressMatcher.isNestedSegwit;
import static com.byow.wallet.byow.utils.AddressMatcher.isSegwit;
import static io.github.bitcoineducation.bitcoinjava.AddressConstants.*;
import static io.github.bitcoineducation.bitcoinjava.ExtendedKeyPrefixes.MAINNET_NESTED_SEGWIT_PREFIX;
import static io.github.bitcoineducation.bitcoinjava.ExtendedKeyPrefixes.MAINNET_SEGWIT_PREFIX;
import static io.github.bitcoineducation.bitcoinjava.Script.P2SH;
import static io.github.bitcoineducation.bitcoinjava.Script.P2WPKH;

@Configuration
public class AddressConfiguration {

    @Value("${initialNumberOfGeneratedAddresses}")
    private int initialNumberOfGeneratedAddresses;

    @Value("${bitcoinEnvironment}")
    private Environment bitcoinEnvironment;

    @Bean({"SEGWIT", "SEGWIT_CHANGE"})
    @Order(0)
    public AddressConfig segwitConfig() {
        return new AddressConfig(
            SEGWIT,
            new LinkedHashMap<>(){{
                put(SEGWIT, "84'/0'/0'/0");
                put(SEGWIT_CHANGE, "84'/0'/0'/1");
            }},
            new SegwitAddressGenerator(),
            Map.of(
                MAINNET, MAINNET_P2WPKH_ADDRESS_PREFIX,
                TESTNET, TESTNET_P2WPKH_ADDRESS_PREFIX,
                REGTEST, REGTEST_P2WPKH_ADDRESS_PREFIX
            ),
            MAINNET_SEGWIT_PREFIX,
            isSegwit(bitcoinEnvironment),
            P2WPKH,
            Script::p2wpkhAddress,
            new SegwitInputBuilder(),
            98,
            0,
            new SegwitTransactionSigner()
        );
    }

    @Bean({"NESTED_SEGWIT", "NESTED_SEGWIT_CHANGE"})
    @Order(1)
    public AddressConfig nestedSegwitConfig() {
        return new AddressConfig(
            NESTED_SEGWIT,
            new LinkedHashMap<>(){{
                put(NESTED_SEGWIT, "49'/0'/0'/0");
                put(NESTED_SEGWIT_CHANGE, "49'/0'/0'/1");
            }},
            new NestedSegwitAddressGenerator(),
            Map.of(
                MAINNET, MAINNET_P2SH_ADDRESS_PREFIX,
                TESTNET, TESTNET_P2SH_ADDRESS_PREFIX,
                REGTEST, TESTNET_P2SH_ADDRESS_PREFIX
            ),
            MAINNET_NESTED_SEGWIT_PREFIX,
            isNestedSegwit(bitcoinEnvironment),
            P2SH,
            Script::nestedSegwitAddress,
            new NestedSegwitInputBuilder(),
            180,
            23,
            new NestedSegwitTransactionSigner()
        );
    }

    @Bean
    Integer initialNumberOfGeneratedAddresses() {
        return initialNumberOfGeneratedAddresses;
    }
}
