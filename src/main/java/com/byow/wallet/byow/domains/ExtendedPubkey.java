package com.byow.wallet.byow.domains;

import java.util.List;

public class ExtendedPubkey {
    private final String key;
    private final String type;
    private List<Address> addresses;

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

    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
    }
}
