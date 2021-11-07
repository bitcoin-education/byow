package com.byow.wallet.byow.observables;

import javafx.beans.property.SimpleStringProperty;
import org.springframework.stereotype.Component;

@Component
public class CurrentWallet {
    private final SimpleStringProperty name = new SimpleStringProperty();

    private final SimpleStringProperty receivingAddress = new SimpleStringProperty();

    public void setName(String name) {
        this.name.set(name);
    }

    public String getName() {
        return name.get();
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public String getReceivingAddress() {
        return receivingAddress.get();
    }

    public SimpleStringProperty receivingAddressProperty() {
        return receivingAddress;
    }

    public void setReceivingAddress(String receivingAddress) {
        this.receivingAddress.set(receivingAddress);
    }
}
