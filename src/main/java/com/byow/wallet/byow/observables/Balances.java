package com.byow.wallet.byow.observables;

import com.byow.wallet.byow.utils.BitcoinFormatter;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Balances {
    private final StringProperty unconfirmedBalance = new SimpleStringProperty();

    private final StringProperty confirmedBalance = new SimpleStringProperty();

    private final StringProperty totalBalance = new SimpleStringProperty();

    public void setBalances(Double unconfirmedBalance, Double confirmedBalance) {
        this.unconfirmedBalance.set(BitcoinFormatter.format(unconfirmedBalance));
        this.confirmedBalance.set(BitcoinFormatter.format(confirmedBalance));
        this.totalBalance.set(BitcoinFormatter.format(unconfirmedBalance + confirmedBalance));
    }

    public void clear() {
        unconfirmedBalance.set(BitcoinFormatter.format(0));
        confirmedBalance.set(BitcoinFormatter.format(0));
        totalBalance.set(BitcoinFormatter.format(0));
    }

    public String getUnconfirmedBalance() {
        return unconfirmedBalance.get();
    }

    public StringProperty unconfirmedBalanceProperty() {
        return unconfirmedBalance;
    }

    public String getConfirmedBalance() {
        return confirmedBalance.get();
    }

    public StringProperty confirmedBalanceProperty() {
        return confirmedBalance;
    }

    public String getTotalBalance() {
        return totalBalance.get();
    }

    public StringProperty totalBalanceProperty() {
        return totalBalance;
    }

}
