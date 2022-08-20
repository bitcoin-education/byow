package com.byow.wallet.byow.observables;

import com.byow.wallet.byow.domains.Address;
import com.sun.javafx.collections.ObservableListWrapper;
import com.sun.javafx.collections.ObservableSetWrapper;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;

import java.util.LinkedHashSet;
import java.util.LinkedList;

public class AddressRows {
    private final ObservableSet<AddressRow> addressRowSet = new ObservableSetWrapper<>(new LinkedHashSet<>());

    private final ObservableList<AddressRow> addressRowList = new ObservableListWrapper<>(new LinkedList<>());

    public AddressRows() {
        SetChangeListener<AddressRow> listener = change -> {
            if (change.wasRemoved()) {
                addressRowList.remove(change.getElementRemoved());
            }
            if (change.wasAdded()) {
                addressRowList.add(change.getElementAdded());
            }
        };
        addressRowSet.addListener(listener);
    }

    public void setAddressRow(Address address) {
        AddressRow addressRow = AddressRow.from(address);
        addressRowSet.remove(addressRow);
        addressRowSet.add(addressRow);
    }

    public ObservableList<AddressRow> getObservableAddressRows() {
        return addressRowList;
    }

    public void clear() {
        addressRowSet.clear();
        addressRowList.clear();
    }
}
