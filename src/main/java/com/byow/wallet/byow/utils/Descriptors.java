package com.byow.wallet.byow.utils;

import com.byow.wallet.byow.domains.ExtendedPubkey;

import java.util.List;
import java.util.Map;

import static com.byow.wallet.byow.domains.AddressType.*;
import static com.byow.wallet.byow.domains.AddressType.NESTED_SEGWIT_CHANGE;

public class Descriptors {
    private static Map<String, String> descMap = Map.of(
        SEGWIT.toString(), "wpkh(%s/*)",
        SEGWIT_CHANGE.toString(), "wpkh(%s/*)",
        NESTED_SEGWIT.toString(), "sh(wpkh(%s/*))",
        NESTED_SEGWIT_CHANGE.toString(), "sh(wpkh(%s/*))"
    );

    public static List<String> getDescriptors(List<ExtendedPubkey> extendedPubkeys) {
        return extendedPubkeys.stream()
            .map(extendedPubkey ->
                descMap.get(extendedPubkey.getType()).formatted(extendedPubkey.getKey())
            ).toList();
    }
}
