package com.byow.wallet.byow.observables;

import com.byow.wallet.byow.domains.Utxo;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.Instant;
import java.util.Objects;

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

    public static TransactionRow from(Utxo utxo) {
        return new TransactionRow(utxo.txid(), Double.toString(utxo.amount()), utxo.confirmations(), Instant.now().toString());
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
