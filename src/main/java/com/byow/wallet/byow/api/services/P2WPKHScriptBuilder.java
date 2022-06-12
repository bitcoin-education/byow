package com.byow.wallet.byow.api.services;

import com.byow.wallet.byow.utils.AddressMatcher;
import io.github.bitcoineducation.bitcoinjava.Bech32;
import io.github.bitcoineducation.bitcoinjava.Script;
import org.springframework.stereotype.Service;

import static com.byow.wallet.byow.domains.AddressType.SEGWIT;

@Service
public class P2WPKHScriptBuilder implements ScriptPubkeyBuilder {
    private final AddressPrefixFactory addressPrefixFactory;

    public P2WPKHScriptBuilder(AddressPrefixFactory addressPrefixFactory) {
        this.addressPrefixFactory = addressPrefixFactory;
    }

    @Override
    public boolean match(String address) {
        return AddressMatcher.isSegwit.test(address);
    }

    @Override
    public Script build(String address) {
        String prefix = addressPrefixFactory.get(SEGWIT);
        return Script.p2wpkhScript(Bech32.decodeToHex(prefix, address));
    }
}
