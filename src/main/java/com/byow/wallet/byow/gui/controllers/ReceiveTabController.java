package com.byow.wallet.byow.gui.controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ReceiveTabController extends Tab {

    public ReceiveTabController(
        @Value("fxml/receive_tab.fxml") Resource fxml,
        ApplicationContext context
    ) throws IOException {
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
