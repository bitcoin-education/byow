package com.byow.wallet.byow.observables;

import com.byow.wallet.byow.domains.ExtendedPubkey;
import javafx.beans.property.SimpleStringProperty;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CurrentWallet {
    private final SimpleStringProperty name = new SimpleStringProperty();

    private final SimpleStringProperty receivingAddress = new SimpleStringProperty();

    private final Addresses addresses = new Addresses();

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
        return addresses.getAddressesAsStrings();
    }

    public void setAddresses(List<ExtendedPubkey> extendedPubkeys) {
        addresses.setAddresses(extendedPubkeys);
    }

    public void setAddressBalance(String address, double sum) {
        addresses.setAddressBalance(address, sum);
    }

    public void setAddressConfirmations(String address, long confirmations) {
        addresses.setAddressConfirmations(address, confirmations);
    }

    public void markAddressAsUsed(String address) {
        addresses.markAsUsed(address);
    }
}
