package com.byow.wallet.byow.api.config;

import com.byow.wallet.byow.domains.AddressConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.byow.wallet.byow.domains.AddressType.SEGWIT;
import static com.byow.wallet.byow.domains.AddressType.SEGWIT_CHANGE;

@Configuration
public class AddressConfiguration {
    @Bean("SEGWIT")
    AddressConfig segwitConfig() {
        return new AddressConfig(SEGWIT, "84'/0'/0'/0");
    }

    @Bean("SEGWIT_CHANGE")
    AddressConfig segwitChangeConfig() {
        return new AddressConfig(SEGWIT_CHANGE, "84'/0'/0'/1");
    }

}