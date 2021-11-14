package com.byow.wallet.byow.domains;

public class Address {

    private final String address;

    private final Long index;

    private boolean used;

    private double balance;

    private long confirmations;

    public Address(String address, Long index) {
        this.address = address;
        this.index = index;
    }

    public String getAddress() {
        return address;
    }

    public Long getIndex() {
        return index;
    }

    public boolean getUsed() {
        return used;
    }

    public void markAsUsed() {
        used = true;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public double getBalance() {
        return balance;
    }

    public void setConfirmations(long confirmations) {
        this.confirmations = confirmations;
    }

    public long getConfirmations() {
        return confirmations;
    }
}
