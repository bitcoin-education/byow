package com.byow.wallet.byow.node.services;

import com.byow.wallet.byow.api.services.AddressConfigFinder;
import com.byow.wallet.byow.api.services.AddressPrefixFactory;
import io.github.bitcoineducation.bitcoinjava.Script;
import io.github.bitcoineducation.bitcoinjava.TransactionOutput;
import org.springframework.stereotype.Service;


@Service
public class AddressParser {
    private final AddressPrefixFactory addressPrefixFactory;

    private final AddressConfigFinder addressConfigFinder;

    public AddressParser(AddressPrefixFactory addressPrefixFactory, AddressConfigFinder addressConfigFinder) {
        this.addressPrefixFactory = addressPrefixFactory;
        this.addressConfigFinder = addressConfigFinder;
    }

    public String parse(TransactionOutput transactionOutput) {
        Script scriptPubkey = transactionOutput.getScriptPubkey();
        return addressConfigFinder.findByScriptPubkeyType(scriptPubkey.getType())
            .map(addressConfig -> addressConfig.addressParser().apply(scriptPubkey, addressPrefixFactory.get(addressConfig.addressType())))
            .orElse("");
    }
}
