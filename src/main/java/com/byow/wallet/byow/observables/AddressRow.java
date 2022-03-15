package com.byow.wallet.byow.observables;

import com.byow.wallet.byow.domains.Address;
import com.byow.wallet.byow.utils.BitcoinFormatter;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.Objects;

public class AddressRow {
    private final StringProperty balance = new SimpleStringProperty();

    private final LongProperty confirmations = new SimpleLongProperty();

    private final StringProperty address = new SimpleStringProperty();

    public AddressRow(String balance, long confirmations, String address) {
        this.balance.set(balance);
        this.confirmations.set(confirmations);
        this.address.set(address);
    }

    public static AddressRow from(Address address) {
        return new AddressRow(BitcoinFormatter.format(address.getBalance()), address.getConfirmations(), address.getAddress());
    }

    public String getBalance() {
        return balance.get();
    }

    public StringProperty balanceProperty() {
        return balance;
    }

    public long getConfirmations() {
        return confirmations.get();
    }

    public LongProperty confirmationsProperty() {
        return confirmations;
    }

    public String getAddress() {
        return address.get();
    }

    public StringProperty addressProperty() {
        return address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AddressRow that = (AddressRow) o;
        return getAddress().equals(that.getAddress());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAddress());
    }
}
