package com.byow.wallet.byow.domains;

import java.math.BigDecimal;

public final class Address {
    private final String address;
    private final Long index;
    private BigDecimal balance;
    private long confirmations;
    private boolean used;
    private final AddressType addressType;
    private boolean frozen;

    public Address(String address, Long index, BigDecimal balance, long confirmations, AddressType addressType) {
        this.address = address;
        this.index = index;
        this.balance = balance;
        this.confirmations = confirmations;
        this.addressType = addressType;
    }

    public String getAddress() {
        return address;
    }

    public Long getIndex() {
        return index;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public void setConfirmations(long confirmations) {
        this.confirmations = confirmations;
    }

    public void markAsUsed() {
        used = true;
    }

    public long getConfirmations() {
        return confirmations;
    }

    public AddressType getAddressType() {
        return addressType;
    }

    public boolean isUsed() {
        return used;
    }

    public boolean isFrozen() {
        return frozen;
    }

    public void freeze() {
        frozen = true;
    }

    public void unfreeze() {
        frozen = false;
    }
}
