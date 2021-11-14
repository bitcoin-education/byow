package com.byow.wallet.byow.observables;

import com.byow.wallet.byow.domains.Address;
import com.byow.wallet.byow.domains.AddressType;
import com.byow.wallet.byow.domains.ExtendedPubkey;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
public class CurrentWallet {
    private final SimpleStringProperty name = new SimpleStringProperty();

    private final SimpleStringProperty receivingAddress = new SimpleStringProperty();

    private final Addresses addresses = new Addresses();

    private Date createdAt;

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
        return addresses.getAllAddressesAsStrings();
    }

    public void setAddresses(List<ExtendedPubkey> extendedPubkeys) {
        addresses.setAddresses(extendedPubkeys);
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public long findNextAddressIndex(AddressType addressType) {
        return addresses.findNextAddressIndex(addressType);
    }

    public String getAddressAt(long index, AddressType addressType) {
        return addresses.getAddressAt(index, addressType);
    }

    public void setAddressConfirmations(String address, long confirmations) {
        addresses.setAddressConfirmations(address, confirmations);
    }

    public void setAddressBalance(String address, double sum) {
        addresses.setAddressBalance(address, sum);
    }

    public void markAddressAsUsed(String address) {
        addresses.markAsUsed(address);
    }

    public ObservableList<AddressRow> getObservableAddressRows() {
        return addresses.getObservableAddressRows();
    }
}
