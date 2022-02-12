package com.byow.wallet.byow.gui.services;

import com.byow.wallet.byow.domains.Wallet;
import com.byow.wallet.byow.observables.CurrentWallet;
import org.springframework.stereotype.Service;

@Service
public class UpdateCurrentWalletService {
    private final CurrentWallet currentWallet;

    public UpdateCurrentWalletService(CurrentWallet currentWallet) {
        this.currentWallet = currentWallet;
    }

    public void update(Wallet wallet) {
        currentWallet.setName(wallet.name());
        currentWallet.setAddresses(wallet.extendedPubkeys());
        currentWallet.setReceivingAddress(wallet.extendedPubkeys().get(0).getAddresses().get(0).getAddress());
    }
}
