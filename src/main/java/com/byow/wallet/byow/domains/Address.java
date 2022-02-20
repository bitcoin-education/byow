package com.byow.wallet.byow.domains;

public final class Address {
    private final String address;
    private final Long index;
    private double balance;
    private long confirmations;
    private boolean used;
    private final AddressType addressType;

    public Address(String address, Long index, double balance, long confirmations, AddressType addressType) {
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

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
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
}
