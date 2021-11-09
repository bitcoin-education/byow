package com.byow.wallet.byow.observables;

import com.byow.wallet.byow.domains.Address;
import javafx.beans.property.SimpleStringProperty;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
public class CurrentWallet {
    private final SimpleStringProperty name = new SimpleStringProperty();

    private final SimpleStringProperty receivingAddress = new SimpleStringProperty();

    private List<List<Address>> addressLists = List.of();


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

    public List<String> getAddressesAsStrings() {
        return addressLists.stream()
            .flatMap(Collection::stream)
            .map(Address::address)
            .toList();
    }

    public void setAddressLists(List<List<Address>> addressLists) {
        this.addressLists = addressLists;
    }
}
