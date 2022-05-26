package com.byow.wallet.byow.observables;

import com.byow.wallet.byow.domains.Address;
import com.byow.wallet.byow.domains.AddressType;
import com.byow.wallet.byow.domains.ExtendedPubkey;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

@Component
public class CurrentWallet {
    private final SimpleStringProperty name = new SimpleStringProperty();

    private final SimpleStringProperty receivingAddress = new SimpleStringProperty();

    private String changeAddress;

    private final Addresses addresses = new Addresses();

    private final AddressRows addressRows = new AddressRows();

    private List<ExtendedPubkey> extendedPubkeys;

    private final TransactionRows transactionRows = new TransactionRows();

    private final Balances balances = new Balances();

    private String mnemonicSeed;

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

    public void clearAddressRows() {
        addressRows.clear();
    }

    public void setAddressBalance(String address, BigDecimal sum) {
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

    public int getAddressesCount(AddressType addressType) {
        return addresses.getAddressesCount(addressType);
    }

    public List<ExtendedPubkey> getExtendedPubkeys() {
        return extendedPubkeys;
    }

    public void setExtendedPubkeys(List<ExtendedPubkey> extendedPubkeys) {
        this.extendedPubkeys = extendedPubkeys;
    }

    public List<String> getAddressesAsStrings(long fromIndex, long toIndex) {
        return addresses.getAddressesAsStrings(fromIndex, toIndex);
    }

    public void addTransactionRows(List<TransactionRow> transactionRows) {
        this.transactionRows.addTransactionRows(transactionRows);
    }

    public void addTransactionRow(TransactionRow transactionRow) {
        this.transactionRows.addTransactionRow(transactionRow);
    }

    public ObservableList<TransactionRow> getObservableTransactionRows() {
        return transactionRows.getObservableTransactionRowList();
    }

    public void clearTransactions() {
        transactionRows.clear();
    }

    public List<String> getTransactionIds() {
        return transactionRows.getTransactionIds();
    }

    public void setBalances(BigDecimal unconfirmedBalance, BigDecimal confirmedBalance) {
        balances.setBalances(unconfirmedBalance, confirmedBalance);
    }

    public Balances getBalances() {
        return balances;
    }

    public void clearBalances() {
        balances.clear();
    }

    public String getChangeAddress() {
        return changeAddress;
    }

    public void setChangeAddress(String changeAddress) {
        this.changeAddress = changeAddress;
    }

    public Address getAddress(String address) {
        return addresses.getAddress(address);
    }

    public String getMnemonicSeed() {
        return mnemonicSeed;
    }

    public void setMnemonicSeed(String mnemonicSeed) {
        this.mnemonicSeed = mnemonicSeed;
    }

}
