package com.byow.wallet.byow.gui.services;

import com.byow.wallet.byow.domains.Utxo;
import com.byow.wallet.byow.observables.CurrentWallet;
import javafx.application.Platform;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;

@Service
public class UpdateCurrentWalletAddressesService {

    private final CurrentWallet currentWallet;

    public UpdateCurrentWalletAddressesService(
        CurrentWallet currentWallet
    ) {
        this.currentWallet = currentWallet;
    }

    public void update(List<Utxo> utxos) {
        Map<String, List<Utxo>> groupedUtxos = utxos.stream().collect(groupingBy(Utxo::address));
        groupedUtxos.forEach((address, utxoList) -> {
            setBalance(address, utxoList);
            setConfirmations(address, utxoList);
            Platform.runLater(() -> currentWallet.setAddressRow(address));
        });
    }

    private void setConfirmations(String address, List<Utxo> utxoList) {
        long confirmations = utxoList.stream()
            .mapToLong(Utxo::confirmations)
            .min()
            .getAsLong();
        currentWallet.setAddressConfirmations(address, confirmations);
    }

    private void setBalance(String address, List<Utxo> utxoList) {
        BigDecimal sum = utxoList.stream()
            .map(Utxo::amount)
            .reduce(BigDecimal::add)
            .orElse(BigDecimal.ZERO);
        currentWallet.setAddressBalance(address, sum);
    }
}
