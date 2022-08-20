package com.byow.wallet.byow.domains;

import java.util.ArrayList;

public class ExtendedPubkey {
    private final String key;
    private final String type;
    private final ArrayList<Address> addresses = new ArrayList<>();

    public ExtendedPubkey(String key, String type) {
        this.key = key;
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public String getType() {
        return type;
    }

    public ArrayList<Address> getAddresses() {
        return addresses;
    }

    public void addAddresses(ArrayList<Address> addresses) {
        this.addresses.addAll(addresses);
    }
}
