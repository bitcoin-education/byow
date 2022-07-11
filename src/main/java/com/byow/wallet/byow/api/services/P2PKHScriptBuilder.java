package com.byow.wallet.byow.api.services;

import io.github.bitcoineducation.bitcoinjava.Base58;
import io.github.bitcoineducation.bitcoinjava.Script;
import org.springframework.stereotype.Service;

@Service
public class P2PKHScriptBuilder implements ScriptPubkeyBuilder {
    @Override
    public Script build(String address) {
        return Script.p2pkhScript(Base58.decodeWithChecksumToHex(address));
    }
}
