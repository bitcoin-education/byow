package com.byow.wallet.byow.gui.services;

import com.byow.wallet.byow.domains.node.NodeTransaction;
import com.byow.wallet.byow.observables.CurrentWallet;
import com.byow.wallet.byow.observables.TransactionRow;
import javafx.application.Platform;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

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
            .map(this::buildNodeTransaction)
            .map(TransactionRow::from)
            .toList();
        Platform.runLater(() -> currentWallet.addTransactionRows(transactionRows));
    }

    private NodeTransaction buildNodeTransaction(List<NodeTransaction> nodeTransactionList) {
        BigDecimal txFee = nodeTransactionList.stream()
            .map(NodeTransaction::fee)
            .filter(fee -> !isNull(fee))
            .findFirst()
            .orElse(BigDecimal.ZERO);
        BigDecimal amount = nodeTransactionList.stream()
            .map(NodeTransaction::amount)
            .reduce(BigDecimal::add)
            .orElse(BigDecimal.ZERO);
        return new NodeTransaction(
            nodeTransactionList.get(0).txid(),
            nodeTransactionList.get(0).confirmations(),
            amount,
            txFee,
            nodeTransactionList.get(0).address(),
            nodeTransactionList.get(0).time()
        );
    }
}
