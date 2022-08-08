package com.byow.wallet.byow.gui.services;

import com.byow.wallet.byow.domains.node.NodeTransaction;
import com.byow.wallet.byow.observables.CurrentWallet;
import com.byow.wallet.byow.observables.TransactionRow;
import javafx.application.Platform;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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
            .collect(Collectors.groupingBy(NodeTransaction::txid))
            .values()
            .stream()
            .map(TransactionRow::from)
            .toList();
        Platform.runLater(() -> currentWallet.addTransactionRows(transactionRows));
    }
}
