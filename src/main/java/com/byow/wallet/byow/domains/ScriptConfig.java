package com.byow.wallet.byow.domains;

import com.byow.wallet.byow.api.services.ScriptPubkeyBuilder;

import java.util.function.Predicate;

public record ScriptConfig(
    ScriptPubkeyBuilder scriptPubkeyBuilder,
    int scriptPubkeySize,
    Predicate<String> addressMatcher
) {
}
