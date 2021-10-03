package com.byow.wallet.byow.gui.services;

import com.byow.wallet.byow.domains.Address;
import com.byow.wallet.byow.domains.Wallet;
import com.byow.wallet.byow.observables.CurrentWallet;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class UpdateCurrentWalletService {
    private final CurrentWallet currentWallet;

    public UpdateCurrentWalletService(CurrentWallet currentWallet) {
        this.currentWallet = currentWallet;
    }

    public void update(Wallet wallet) {
        currentWallet.setName(wallet.name());
        currentWallet.setReceivingAddress(wallet.extendedPubkeys().get(0).getAddresses().get(0).address());
        List<Address> addresses = wallet.extendedPubkeys()
            .stream()
            .flatMap(it -> it.getAddresses().stream())
            .toList();
    }
}
