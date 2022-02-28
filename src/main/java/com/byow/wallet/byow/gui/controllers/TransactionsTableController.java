package com.byow.wallet.byow.gui.controllers;

import com.byow.wallet.byow.domains.Transaction;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TableView;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class TransactionsTableController extends TableView<Transaction> {
    public TransactionsTableController(
        @Value("fxml/transactions_table.fxml") Resource fxml,
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
