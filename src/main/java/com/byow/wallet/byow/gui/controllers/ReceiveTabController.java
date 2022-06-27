package com.byow.wallet.byow.gui.controllers;

import com.byow.wallet.byow.domains.AddressType;
import com.byow.wallet.byow.observables.CurrentWallet;
import javafx.collections.MapChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ReceiveTabController extends Tab {
    private final CurrentWallet currentWallet;

    @FXML
    private TextField receivingAddress;

    public ReceiveTabController(
        @Value("fxml/receive_tab.fxml") Resource fxml,
        ApplicationContext context,
        CurrentWallet currentWallet
    ) throws IOException {
        this.currentWallet = currentWallet;
        FXMLLoader fxmlLoader = new FXMLLoader(
            fxml.getURL(),
            null,
            null,
            context::getBean
        );
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);
        fxmlLoader.load();
    }

    public void initialize() {
        currentWallet.getObservableReceivingAddresses().addListener((MapChangeListener<AddressType, String>) change -> {
            if(change.getKey().equals(AddressType.SEGWIT)) {
                receivingAddress.setText(change.getValueAdded());
            }
        });
    }
}
