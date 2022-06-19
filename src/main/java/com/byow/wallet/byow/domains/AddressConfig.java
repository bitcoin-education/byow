package com.byow.wallet.byow.domains;

import com.byow.wallet.byow.api.services.AddressGenerator;
import io.github.bitcoineducation.bitcoinjava.ExtendedKeyPrefix;
import io.github.bitcoineducation.bitcoinjava.Script;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Predicate;

public record AddressConfig(
    AddressType addressType,
    Map<AddressType, String> derivationPaths,
    AddressGenerator addressGenerator,
    Map<Environment, String> addressPrefixes,
    ExtendedKeyPrefix extendedKeyPrefix,
    Predicate<String> addressMatcher,
    String scriptPubkeyType,
    BiFunction<Script, String, String> addressParser
) {
}
