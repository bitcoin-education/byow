package com.byow.wallet.byow.api.services;

import io.github.bitcoineducation.bitcoinjava.Bech32;
import io.github.bitcoineducation.bitcoinjava.Script;
import org.springframework.stereotype.Service;

import static io.github.bitcoineducation.bitcoinjava.AddressConstants.*;

@Service
public class P2WPKHScriptBuilder implements ScriptPubkeyBuilder {
    @Override
    public Script build(String address) {
        String prefix = parsePrefix(address);
        return Script.p2wpkhScript(Bech32.decodeToHex(prefix, address));
    }

    private String parsePrefix(String address) {
        if (address.startsWith(TESTNET_P2WPKH_ADDRESS_PREFIX)) {
            return TESTNET_P2WPKH_ADDRESS_PREFIX;
        }
        if (address.startsWith(REGTEST_P2WPKH_ADDRESS_PREFIX)) {
            return REGTEST_P2WPKH_ADDRESS_PREFIX;
        }
        return MAINNET_P2WPKH_ADDRESS_PREFIX;
    }
}
