package com.byow.wallet.byow.observables;

import com.byow.wallet.byow.domains.Address;
import com.byow.wallet.byow.domains.AddressType;
import com.byow.wallet.byow.domains.ExtendedPubkey;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
                        .collect(toMap(Address::address, Function.identity(), (a, b) -> a, LinkedHashMap::new))
                )
            );
    }
}
