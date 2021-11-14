package com.byow.wallet.byow.observables;

import com.byow.wallet.byow.domains.Address;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class AddressRow {

    private final StringProperty balance = new SimpleStringProperty();

    private final IntegerProperty confirmations = new SimpleIntegerProperty();

    private final StringProperty address = new SimpleStringProperty();

    public AddressRow(String balance, int confirmations, String address) {
        this.balance.set(balance);
        this.confirmations.set(confirmations);
        this.address.set(address);
    }

    public static AddressRow from(Address address) {
        return new AddressRow("0", 0, address.getAddress());
    }

    public String getBalance() {
        return balance.get();
    }

    public StringProperty balanceProperty() {
        return balance;
    }

    public int getConfirmations() {
        return confirmations.get();
    }

    public IntegerProperty confirmationsProperty() {
        return confirmations;
    }

    public String getAddress() {
        return address.get();
    }

    public StringProperty addressProperty() {
        return address;
    }

    public void setConfirmations(Long confirmations) {
        this.confirmations.set(confirmations.intValue());
    }

    public void setBalance(Double balance) {
        this.balance.set(balance.toString());
    }
}
