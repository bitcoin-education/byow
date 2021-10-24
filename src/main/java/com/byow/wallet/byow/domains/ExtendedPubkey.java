package com.byow.wallet.byow.domains;

public class ExtendedPubkey {
    private final String key;
    private final String type;

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
}
