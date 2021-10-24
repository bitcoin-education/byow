package com.byow.wallet.byow.api.config;

import com.byow.wallet.byow.domains.AddressConfig;
import com.byow.wallet.byow.domains.AddressType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AddressConfiguration {

    @Bean("SEGWIT")
    AddressConfig segwitConfig() {
        return new AddressConfig(AddressType.SEGWIT, "84'/0'/0'/0");
    }

    @Bean("SEGWIT_CHANGE")
    AddressConfig segwitChangeConfig() {
        return new AddressConfig(AddressType.SEGWIT_CHANGE, "84'/0'/0'/1");
    }
}
