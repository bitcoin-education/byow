package com.byow.wallet.byow.gui.services;

import com.byow.wallet.byow.domains.Utxo;
import com.byow.wallet.byow.observables.CurrentWallet;
import com.byow.wallet.byow.observables.TransactionRow;
import javafx.application.Platform;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UpdateCurrentWalletTransactionsService {
    private final CurrentWallet currentWallet;

    public UpdateCurrentWalletTransactionsService(CurrentWallet currentWallet) {
        this.currentWallet = currentWallet;
    }

    public void update(List<Utxo> utxos) {
        List<TransactionRow> transactionRows = utxos.stream()
            .filter(utxo -> currentWallet.getAddressesAsStrings().contains(utxo.address()))
            .map(TransactionRow::from)
            .toList();
        Platform.runLater(() -> currentWallet.addTransactionRows(transactionRows));
    }

    public void update(TransactionRow transactionRow) {
        Platform.runLater(() -> currentWallet.addTransactionRow(transactionRow));
    }
}
