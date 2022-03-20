package com.byow.wallet.byow.gui.services;

import com.byow.wallet.byow.observables.CurrentWallet;
import javafx.application.Platform;
import org.springframework.stereotype.Service;

@Service
public class UpdateCurrentWalletBalanceService {
    private final CurrentWallet currentWallet;

    public UpdateCurrentWalletBalanceService(CurrentWallet currentWallet) {
        this.currentWallet = currentWallet;
    }

    public void update() {
        Platform.runLater(() -> {
            Double unconfirmedBalance = currentWallet.getObservableTransactionRows().stream()
                .filter(transactionRow -> transactionRow.getConfirmations() == 0)
                .map(transactionRow -> Double.parseDouble(transactionRow.getBalance()))
                .reduce(Double::sum)
                .orElse(0.0);
            Double confirmedBalance = currentWallet.getObservableTransactionRows().stream()
                .filter(transactionRow -> transactionRow.getConfirmations() > 0)
                .map(transactionRow -> Double.parseDouble(transactionRow.getBalance()))
                .reduce(Double::sum)
                .orElse(0.0);
            currentWallet.setBalances(unconfirmedBalance, confirmedBalance);
        });
    }
}
