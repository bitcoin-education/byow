package com.byow.wallet.byow.api.services;

import io.github.bitcoineducation.bitcoinjava.Script;

public interface ScriptPubkeyBuilder {
    boolean match(String address);

    Script build(String address);
}
