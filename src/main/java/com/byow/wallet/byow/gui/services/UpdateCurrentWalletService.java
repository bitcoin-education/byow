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
        currentWallet.setExtendedPubkeys(wallet.extendedPubkeys());
        currentWallet.setAddresses(wallet.extendedPubkeys());
        currentWallet.clearAddressRows();
        currentWallet.clearTransactions();
        currentWallet.clearBalances();
        currentWallet.setReceivingAddress(wallet.extendedPubkeys().get(0).getAddresses().get(0).getAddress());
        currentWallet.setNestedSegwitReceivingAddress(wallet.extendedPubkeys().get(2).getAddresses().get(0).getAddress());
        currentWallet.setChangeAddress(wallet.extendedPubkeys().get(1).getAddresses().get(0).getAddress());
        currentWallet.setNestedSegwitChangeAddress(wallet.extendedPubkeys().get(3).getAddresses().get(0).getAddress());
        currentWallet.setMnemonicSeed(wallet.mnemonicSeed());
    }
}
