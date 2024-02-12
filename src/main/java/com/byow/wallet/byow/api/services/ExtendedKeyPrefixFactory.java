package com.byow.wallet.byow.api.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.byow.wallet.byow.domains.Environment;
import io.github.bitcoineducation.bitcoinjava.ExtendedKeyPrefix;

import java.util.Map;

import static io.github.bitcoineducation.bitcoinjava.ExtendedKeyPrefixes.MAINNET_PREFIX;
import static io.github.bitcoineducation.bitcoinjava.ExtendedKeyPrefixes.TESTNET_PREFIX;

@Service
public class ExtendedKeyPrefixFactory {
    private final String bitcoinEnvironment;

    private final Map<Environment, ExtendedKeyPrefix> prefixMap = Map.of(
        Environment.MAINNET, MAINNET_PREFIX,
        Environment.TESTNET, TESTNET_PREFIX,
        Environment.REGTEST, TESTNET_PREFIX
    );

    public ExtendedKeyPrefixFactory(@Value("${bitcoinEnvironment}") String bitcoinEnvironment) {
        this.bitcoinEnvironment = bitcoinEnvironment;
    }

    public ExtendedKeyPrefix get() {
        return prefixMap.get(Environment.valueOf(bitcoinEnvironment.toUpperCase()));
    }
}
