package com.byow.wallet.byow.gui.services;

import com.byow.wallet.byow.domains.AddressType;
import com.byow.wallet.byow.domains.Utxo;
import com.byow.wallet.byow.observables.CurrentWallet;
import javafx.application.Platform;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.byow.wallet.byow.domains.AddressType.SEGWIT;


@Service
public class UpdateCurrentWalletAddressesService {
    private final CurrentWallet currentWallet;

    private final static AddressType receivingAddressType = SEGWIT;

    public UpdateCurrentWalletAddressesService(CurrentWallet currentWallet) {
        this.currentWallet = currentWallet;
    }

    public void update(List<Utxo> utxos) {
        Platform.runLater(() -> {
            Map<String, List<Utxo>> groupedUtxos = utxos.stream().collect(Collectors.groupingBy(Utxo::address));
            groupedUtxos.forEach((address, utxoList) -> {
                setBalance(address, utxoList);
                setConfirmations(address, utxoList);
                markAsUsed(address);
            });
            updateReceivingAddress();
        });
    }

    private void updateReceivingAddress() {
        long nextAddressIndex = currentWallet.findNextAddressIndex(receivingAddressType);
        String nextAddress = currentWallet.getAddressAt(nextAddressIndex, receivingAddressType);
        currentWallet.setReceivingAddress(nextAddress);
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

    private void markAsUsed(String address) {
        currentWallet.markAddressAsUsed(address);
    }

}
