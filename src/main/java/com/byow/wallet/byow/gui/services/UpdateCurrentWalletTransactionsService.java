package com.byow.wallet.byow.gui.services;

import com.byow.wallet.byow.domains.node.NodeTransaction;
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

    public void update(TransactionRow transactionRow) {
        Platform.runLater(() -> currentWallet.addTransactionRow(transactionRow));
    }

    public void updateNodeTransactions(List<NodeTransaction> nodeTransactions) {
        List<TransactionRow> transactionRows = nodeTransactions.stream()
            .filter(nodeTransaction -> currentWallet.getAddressesAsStrings().contains(nodeTransaction.address()) || currentWallet.getTransactionIds().contains(nodeTransaction.txid()))
            .map(TransactionRow::from)
            .toList();
        Platform.runLater(() -> currentWallet.addTransactionRows(transactionRows));
    }
}