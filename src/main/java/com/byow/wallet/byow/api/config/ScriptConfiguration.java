package com.byow.wallet.byow.api.config;

import com.byow.wallet.byow.api.services.P2PKHScriptBuilder;
import com.byow.wallet.byow.api.services.P2SHScriptBuilder;
import com.byow.wallet.byow.api.services.P2WPKHScriptBuilder;
import com.byow.wallet.byow.domains.Environment;
import com.byow.wallet.byow.domains.ScriptConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.byow.wallet.byow.utils.AddressMatcher.*;

@Configuration
public class ScriptConfiguration {

    @Value("${bitcoinEnvironment}")
    private Environment bitcoinEnvironment;

    @Bean
    public ScriptConfig P2WPKHConfig() {
        return new ScriptConfig(
            new P2WPKHScriptBuilder(),
            22,
            isSegwit(bitcoinEnvironment)
        );
    }

    @Bean
    public ScriptConfig P2SHConfig() {
        return new ScriptConfig(
            new P2SHScriptBuilder(),
            23,
            isNestedSegwit(bitcoinEnvironment)
        );
    }

    @Bean
    public ScriptConfig P2PKHConfig() {
        return new ScriptConfig(
            new P2PKHScriptBuilder(),
            25,
            isLegacy(bitcoinEnvironment)
        );
    }

}
