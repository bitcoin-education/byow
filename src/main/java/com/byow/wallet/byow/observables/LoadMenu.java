package com.byow.wallet.byow.observables;

import com.byow.wallet.byow.database.entities.WalletEntity;
import com.sun.javafx.collections.ObservableSetWrapper;
import javafx.collections.ObservableSet;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;

@Component
public class LoadMenu {
    private final ObservableSet<WalletEntity> menuItems = new ObservableSetWrapper<>(new LinkedHashSet<>());

    public ObservableSet<WalletEntity> getObservableMenuItems() {
        return menuItems;
    }
}
