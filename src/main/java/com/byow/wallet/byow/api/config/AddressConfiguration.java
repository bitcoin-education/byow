package com.byow.wallet.byow.api.config;

import com.byow.wallet.byow.domains.AddressConfig;
import com.byow.wallet.byow.domains.AddressType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static io.github.bitcoineducation.bitcoinjava.AddressConstants.*;

@Configuration
public class AddressConfiguration {
    @Value("${bitcoinEnvironment}")
    private String bitcoinEnvironment;

    @Bean("SEGWIT")
    AddressConfig segwitConfig() {
        return new AddressConfig(AddressType.SEGWIT, "84'/0'/0'/0");
    }

    @Bean("SEGWIT_CHANGE")
    AddressConfig segwitChangeConfig() {
        return new AddressConfig(AddressType.SEGWIT_CHANGE, "84'/0'/0'/1");
    }

    @Bean
    Integer initialNumberOfGeneratedAddresses() {
        return 20;
    }

    @Bean
    public String getP2WPKHAddressPrefix() {
        return switch (bitcoinEnvironment) {
            case "mainnet" -> MAINNET_P2WPKH_ADDRESS_PREFIX;
            case "testnet" -> TESTNET_P2WPKH_ADDRESS_PREFIX;
            case "regtest" -> REGTEST_P2WPKH_ADDRESS_PREFIX;
            default -> throw new RuntimeException("invalid environment");
        };
    }
}
