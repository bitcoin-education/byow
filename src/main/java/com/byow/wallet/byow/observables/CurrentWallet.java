package com.byow.wallet.byow.observables;

import com.byow.wallet.byow.domains.AddressType;
import com.byow.wallet.byow.domains.ExtendedPubkey;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CurrentWallet {
    private final SimpleStringProperty name = new SimpleStringProperty();

    private final SimpleStringProperty receivingAddress = new SimpleStringProperty();

    private final Addresses addresses = new Addresses();

    private AddressRows addressRows = new AddressRows();

    private List<ExtendedPubkey> extendedPubkeys;

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

    public List<String> getAddressesAsStrings(long fromIndex, long toIndex) {
        return addresses.getAddressesAsStrings(fromIndex, toIndex);
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

    public void setAddressRow(String address) {
        addressRows.setAddressRow(addresses.getAddress(address));
    }

    public ObservableList<AddressRow> getObservableAddressRows() {
        return addressRows.getObservableAddressRows();
    }

    public AddressType getAddressType(String address) {
        return addresses.getAddressType(address);
    }

    public long findNextAddressIndex(AddressType addressType) {
        return addresses.findNextAddressIndex(addressType);
    }

    public String getAddressAt(long addressIndex, AddressType addressType) {
        return addresses.getAddressAt(addressIndex, addressType);
    }

    public void setExtendedPubkeys(List<ExtendedPubkey> extendedPubkeys) {
        this.extendedPubkeys = extendedPubkeys;
    }

    public List<ExtendedPubkey> getExtendedPubkeys() {
        return extendedPubkeys;
    }

    public void clearAddressRows() {
        addressRows.clear();
    }

    public int getAddressCount(AddressType addressType) {
        return addresses.getAddressCount(addressType);
    }
}
