package com.byow.wallet.byow.node.services;

import com.byow.wallet.byow.api.services.AddressPrefixFactory;
import io.github.bitcoineducation.bitcoinjava.Script;
import io.github.bitcoineducation.bitcoinjava.TransactionOutput;
import org.springframework.stereotype.Service;

import static com.byow.wallet.byow.domains.AddressType.SEGWIT;
import static io.github.bitcoineducation.bitcoinjava.Script.P2WPKH;

@Service
public class AddressParser {

    private final AddressPrefixFactory addressPrefixFactory;

    public AddressParser(AddressPrefixFactory addressPrefixFactory) {
        this.addressPrefixFactory = addressPrefixFactory;
    }

    public String parse(TransactionOutput transactionOutput) {
        Script scriptPubkey = transactionOutput.getScriptPubkey();
        return switch (scriptPubkey.getType()) {
            case P2WPKH -> scriptPubkey.p2wpkhAddress(addressPrefixFactory.get(SEGWIT));
            default -> "";
        };
    }
}