package com.byow.wallet.byow.api.services;

import io.github.bitcoineducation.bitcoinjava.Script;

public interface ScriptPubkeyBuilder {
    Script build(String address);
}
