package com.byow.wallet.byow.gui.services;

import com.byow.wallet.byow.observables.CurrentWallet;
import javafx.application.Platform;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class UpdateCurrentWalletBalanceService {
    private final CurrentWallet currentWallet;

    public UpdateCurrentWalletBalanceService(CurrentWallet currentWallet) {
        this.currentWallet = currentWallet;
    }

    public void update() {
        Platform.runLater(() -> {
            BigDecimal unconfirmedBalance = currentWallet.getObservableTransactionRows().stream()
                .filter(transactionRow -> transactionRow.getConfirmations() == 0)
                .map(transactionRow -> new BigDecimal(transactionRow.getBalance()))
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);
            BigDecimal confirmedBalance = currentWallet.getObservableTransactionRows().stream()
                .filter(transactionRow -> transactionRow.getConfirmations() > 0)
                .map(transactionRow -> new BigDecimal(transactionRow.getBalance()))
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);
            currentWallet.setBalances(unconfirmedBalance, confirmedBalance);
        });
    }
}
