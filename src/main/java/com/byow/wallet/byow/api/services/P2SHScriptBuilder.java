package com.byow.wallet.byow.api.services;

import com.byow.wallet.byow.utils.AddressMatcher;
import io.github.bitcoineducation.bitcoinjava.Base58;
import io.github.bitcoineducation.bitcoinjava.Script;
import org.springframework.stereotype.Service;

@Service
public class P2SHScriptBuilder implements ScriptPubkeyBuilder {
    @Override
    public boolean match(String address) {
        return AddressMatcher.isNestedSegwit.test(address);
    }

    @Override
    public Script build(String address) {
        return Script.p2shScript(Base58.decodeWithChecksumToHex(address));
    }
}
