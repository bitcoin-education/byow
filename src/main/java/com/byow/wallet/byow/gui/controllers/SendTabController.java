package com.byow.wallet.byow.gui.controllers;

import com.byow.wallet.byow.observables.CurrentWallet;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SendTabController extends Tab {
    private final CurrentWallet currentWallet;

    public SendTabController(
        @Value("fxml/send_tab.fxml") Resource fxml,
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
}
