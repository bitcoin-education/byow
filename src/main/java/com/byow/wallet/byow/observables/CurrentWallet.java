package com.byow.wallet.byow.observables;

import javafx.beans.property.SimpleStringProperty;
import org.springframework.stereotype.Component;

@Component
public class CurrentWallet {
    private final SimpleStringProperty name = new SimpleStringProperty();

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

}
