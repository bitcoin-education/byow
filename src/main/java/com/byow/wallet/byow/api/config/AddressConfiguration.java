package com.byow.wallet.byow.api.config;

import com.byow.wallet.byow.domains.AddressConfig;
import com.byow.wallet.byow.domains.AddressType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AddressConfiguration {

    @Value("${initialNumberOfGeneratedAddresses}")
    private int initialNumberOfGeneratedAddresses;

    @Bean("SEGWIT")
    AddressConfig segwitConfig() {
        return new AddressConfig(AddressType.SEGWIT, "84'/0'/0'/0");
    }

    @Bean("SEGWIT_CHANGE")
    AddressConfig segwitChangeConfig() {
        return new AddressConfig(AddressType.SEGWIT_CHANGE, "84'/0'/0'/1");
    }

    @Bean("NESTED_SEGWIT")
    AddressConfig nestedSegwitConfig() {
        return new AddressConfig(AddressType.NESTED_SEGWIT, "49'/0'/0'/0");
    }

    @Bean("NESTED_SEGWIT_CHANGE")
    AddressConfig nestedSegwitChangeConfig() {
        return new AddressConfig(AddressType.NESTED_SEGWIT_CHANGE, "49'/0'/0'/1");
    }

    @Bean
    Integer initialNumberOfGeneratedAddresses() {
        return initialNumberOfGeneratedAddresses;
    }
}
