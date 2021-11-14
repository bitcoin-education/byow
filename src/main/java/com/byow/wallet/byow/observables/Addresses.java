package com.byow.wallet.byow.observables;

import com.byow.wallet.byow.domains.Address;
import com.byow.wallet.byow.domains.AddressType;
import com.byow.wallet.byow.domains.ExtendedPubkey;
import com.sun.javafx.collections.ObservableListWrapper;
import javafx.beans.Observable;
import javafx.collections.ObservableList;

import java.util.*;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

public class Addresses {
    private Map<AddressType, LinkedHashMap<String, Address>> addresses = new HashMap<>();

    private final LinkedHashMap<String, AddressRow> addressRows = new LinkedHashMap<>();

    private final ObservableList<AddressRow> observableAddressRows = new ObservableListWrapper<>(new LinkedList<>(),
        addressRow -> new Observable[]{addressRow.balanceProperty(), addressRow.confirmationsProperty()}
    );

    public List<String> getAllAddressesAsStrings() {
        return addresses.values()
            .stream()
            .flatMap(addressMap -> addressMap.keySet().stream())
            .toList();
    }

    public long findNextAddressIndex(AddressType addressType) {
        List<Address> addressList = addresses.get(addressType)
            .values()
            .stream()
            .toList();
        return getLastUsedAddressIndex(new ArrayList<>(addressList)) + 1;
    }

    private long getLastUsedAddressIndex(List<Address> addressList) {
        Collections.reverse(addressList);
        Address address = addressList.stream()
            .filter(Address::getUsed)
            .findFirst()
            .orElse(null);
        if (address == null) {
            return -1;
        }
        return address.getIndex();
    }

    public void setAddresses(List<ExtendedPubkey> extendedPubkeys) {
        addresses = extendedPubkeys.stream()
            .collect(
                toMap(
                    extendedPubkey -> AddressType.valueOf(extendedPubkey.getType()),
                    extendedPubkey -> extendedPubkey.getAddresses()
                        .stream()
                        .collect(toMap(Address::getAddress, Function.identity(), (a, b) -> a, LinkedHashMap::new))
                )
            );
        setAddressRows();
    }

    private void setAddressRows() {
        Map<String, AddressRow> addressRowMap = addresses.values()
            .stream()
            .flatMap(addressMap -> addressMap.values().stream())
            .map(AddressRow::from)
            .collect(toMap(AddressRow::getAddress, Function.identity()));
        addressRows.clear();
        addressRows.putAll(addressRowMap);
        observableAddressRows.clear();
        observableAddressRows.setAll(addressRowMap.values());
    }

    public String getAddressAt(Long index, AddressType addressType) {
        return addresses.get(addressType)
            .values()
            .stream()
            .toList()
            .get(index.intValue())
            .getAddress();
    }

    public Map<String, Address> getAllAddressesAsMap() {
        return addresses.values()
            .stream()
            .flatMap(addressMap -> addressMap.entrySet().stream())
            .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public void setAddressConfirmations(String address, long confirmations) {
        getAllAddressesAsMap().get(address).setConfirmations(confirmations);
        addressRows.get(address).setConfirmations(confirmations);
    }

    public void setAddressBalance(String address, double sum) {
        getAllAddressesAsMap().get(address).setBalance(sum);
        addressRows.get(address).setBalance(sum);
    }

    public void markAsUsed(String address) {
        getAllAddressesAsMap().get(address).markAsUsed();
    }

    public ObservableList<AddressRow> getObservableAddressRows() {
        return observableAddressRows;
    }
}
