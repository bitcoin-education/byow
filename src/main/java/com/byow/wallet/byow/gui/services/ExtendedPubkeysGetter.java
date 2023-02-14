package com.byow.wallet.byow.gui.services;

import com.byow.wallet.byow.domains.ExtendedPubkey;
import com.byow.wallet.byow.observables.CurrentWallet;
import com.byow.wallet.byow.utils.ExtendedPubkeysSerializer;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExtendedPubkeysGetter {

    private final CurrentWallet currentWallet;

    public ExtendedPubkeysGetter(CurrentWallet currentWallet) {
        this.currentWallet = currentWallet;
    }

    public String get() {
        List<ExtendedPubkey> extendedPubkeys = currentWallet.getExtendedPubkeys();
        return ExtendedPubkeysSerializer.serialize(extendedPubkeys);
    }

}
