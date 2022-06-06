package com.byow.wallet.byow.node.services;

import com.byow.wallet.byow.api.services.AddressPrefixFactory;
import io.github.bitcoineducation.bitcoinjava.*;
import org.springframework.stereotype.Service;

import static com.byow.wallet.byow.domains.AddressType.NESTED_SEGWIT;
import static com.byow.wallet.byow.domains.AddressType.SEGWIT;
import static io.github.bitcoineducation.bitcoinjava.Script.P2SH;
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
            case P2SH -> toP2SH(addressPrefixFactory.get(NESTED_SEGWIT), scriptPubkey);
            default -> "";
        };
    }

    //TODO: mudar para lib
    public String toP2SH(String prefix, Script scriptPubKey) {
        return Base58.encodeWithChecksumFromHex(prefix.concat((String) scriptPubKey.getCommands().get(1)));
    }
}
