package com.byow.wallet.byow.gui.services;

import com.byow.wallet.byow.domains.AddressType;
import com.byow.wallet.byow.domains.Utxo;
import com.byow.wallet.byow.observables.CurrentWallet;
import javafx.application.Platform;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;

@Service
public class UpdateCurrentWalletAddressesService {

    private final CurrentWallet currentWallet;

    public UpdateCurrentWalletAddressesService(CurrentWallet currentWallet) {
        this.currentWallet = currentWallet;
    }

    public void update(List<Utxo> utxos) {
        Map<String, List<Utxo>> groupedUtxos = utxos.stream().collect(groupingBy(Utxo::address));
        groupedUtxos.forEach((address, utxoList) -> {
            setBalance(address, utxoList);
            setConfirmations(address, utxoList);
            markAsUsed(address);
            Platform.runLater(() -> {
                currentWallet.setAddressRow(address);
                updateReceivingAddress(address);
            });
        });
    }

    private void updateReceivingAddress(String address) {
        AddressType addressType = currentWallet.getAddressType(address);
        long nextAddressIndex = currentWallet.findNextAddressIndex(addressType);
        String nextAddress = currentWallet.getAddressAt(nextAddressIndex, addressType);
        currentWallet.setReceivingAddress(nextAddress);
    }

    private void markAsUsed(String address) {
        currentWallet.markAddressAsUsed(address);
    }

    private void setConfirmations(String address, List<Utxo> utxoList) {
        long confirmations = utxoList.stream()
            .mapToLong(Utxo::confirmations)
            .min()
            .getAsLong();
        currentWallet.setAddressConfirmations(address, confirmations);
    }

    private void setBalance(String address, List<Utxo> utxoList) {
        double sum = utxoList.stream()
            .mapToDouble(Utxo::amount)
            .sum();
        currentWallet.setAddressBalance(address, sum);
    }
}
