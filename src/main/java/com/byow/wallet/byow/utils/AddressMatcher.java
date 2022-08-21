package com.byow.wallet.byow.utils;

import com.byow.wallet.byow.domains.Environment;

import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.byow.wallet.byow.domains.Environment.*;

public class AddressMatcher {
    public static Predicate<String> isSegwit(Environment environment) {
        Map<Environment, Predicate<String>> map = Map.of(
            MAINNET, address -> !address.startsWith("bcrt") && address.startsWith("bc"),
            TESTNET, address -> address.startsWith("tb"),
            REGTEST, address -> address.startsWith("bcrt")
        );
        return map.get(environment);
    }

    public static Predicate<String> isNestedSegwit(Environment environment) {
        Map<Environment, Predicate<String>> map = Map.of(
            MAINNET, address -> address.startsWith("3"),
            TESTNET, address -> address.startsWith("2"),
            REGTEST, address -> address.startsWith("2")
        );
        return map.get(environment);
    }

    public static Predicate<String> isLegacy(Environment environment) {
        Map<Environment, Predicate<String>> map = Map.of(
            MAINNET, address -> address.startsWith("1"),
            TESTNET, address -> Stream.of("m", "n").anyMatch(address::startsWith),
            REGTEST, address -> Stream.of("m", "n").anyMatch(address::startsWith)
        );
        return map.get(environment);
    }
}
