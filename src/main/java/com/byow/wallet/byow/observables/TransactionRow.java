package com.byow.wallet.byow.observables;

import com.byow.wallet.byow.domains.TransactionDto;
import com.byow.wallet.byow.domains.node.NodeTransaction;
import com.byow.wallet.byow.utils.BitcoinFormatter;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

import static java.util.Objects.isNull;

public class TransactionRow {
    private final StringProperty id = new SimpleStringProperty();

    private final StringProperty balance = new SimpleStringProperty();

    private final LongProperty confirmations = new SimpleLongProperty();

    private final StringProperty date = new SimpleStringProperty();

    public TransactionRow(String id, String balance, Long confirmations, String date) {
        setId(id);
        setBalance(balance);
        setConfirmations(confirmations);
        setDate(date);
    }

    public static TransactionRow from(TransactionDto transactionDto) {
        try {
            return new TransactionRow(
                transactionDto.transaction().id(),
                BitcoinFormatter.format(transactionDto.totalSpent().negate()),
                0L, Instant.now().toString()
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static TransactionRow from(List<NodeTransaction> nodeTransactions) {
        BigDecimal txFee = nodeTransactions.stream()
            .map(NodeTransaction::fee)
            .filter(fee -> !isNull(fee))
            .findFirst()
            .orElse(BigDecimal.ZERO);
        BigDecimal amount = nodeTransactions.stream()
            .map(NodeTransaction::amount)
            .reduce(BigDecimal::add)
            .orElse(BigDecimal.ZERO)
            .add(txFee);
        return new TransactionRow(nodeTransactions.get(0).txid(), BitcoinFormatter.format(amount), nodeTransactions.get(0).confirmations(), nodeTransactions.get(0).time().toString());
    }

    public String getId() {
        return id.get();
    }

    public StringProperty idProperty() {
        return id;
    }

    public void setId(String id) {
        this.id.set(id);
    }

    public String getBalance() {
        return balance.get();
    }

    public StringProperty balanceProperty() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance.set(balance);
    }

    public long getConfirmations() {
        return confirmations.get();
    }

    public LongProperty confirmationsProperty() {
        return confirmations;
    }

    public void setConfirmations(long confirmations) {
        this.confirmations.set(confirmations);
    }

    public String getDate() {
        return date.get();
    }

    public StringProperty dateProperty() {
        return date;
    }

    public void setDate(String date) {
        this.date.set(date);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransactionRow that = (TransactionRow) o;
        return getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

}
