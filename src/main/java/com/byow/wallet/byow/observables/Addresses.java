package com.byow.wallet.byow.observables;

import com.byow.wallet.byow.domains.Address;
import com.byow.wallet.byow.domains.AddressType;
import com.byow.wallet.byow.domains.ExtendedPubkey;

import java.util.*;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

public class Addresses {
    private Map<AddressType, LinkedHashMap<String, Address>> addresses = new HashMap<>();

    public List<String> getAddressesAsStrings() {
        return addresses.values()
            .stream()
            .flatMap(addressMap -> addressMap.keySet().stream())
            .toList();
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
    }

    public void setAddressBalance(String address, double sum) {
        getAddress(address).setBalance(sum);
    }

    private Map<String, Address> getAddressesAsMap() {
        return addresses.values()
            .stream()
            .flatMap(addressMap -> addressMap.entrySet().stream())
            .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public void setAddressConfirmations(String address, long confirmations) {
        getAddress(address).setConfirmations(confirmations);
    }

    public Address getAddress(String address) {
        return getAddressesAsMap().get(address);
    }

    public void markAsUsed(String address) {
        getAddress(address).markAsUsed();
    }

    public AddressType getAddressType(String address) {
        return getAddress(address).getAddressType();
    }

    public long findNextAddressIndex(AddressType addressType) {
        Address address = addresses.get(addressType)
            .values()
            .stream()
            .sorted(Comparator.comparing(Address::getIndex, Comparator.reverseOrder()))
            .filter(Address::isUsed)
            .findFirst()
            .orElse(null);
        if (address == null) {
            return 0;
        }
        return address.getIndex() + 1;
    }

    public String getAddressAt(Long addressIndex, AddressType addressType) {
        return addresses.get(addressType)
            .values()
            .stream()
            .toList()
            .get(addressIndex.intValue())
            .getAddress();
    }

    public int getAddressesCount(AddressType addressType) {
        return addresses.get(addressType).size();
    }

    public List<String> getAddressesAsStrings(long fromIndex, long toIndex) {
        return addresses.values()
            .stream()
            .flatMap(addressMap -> addressMap.values().stream())
            .filter(address -> address.getIndex() >= fromIndex && address.getIndex() < toIndex)
            .map(Address::getAddress)
            .toList();
    }
}
