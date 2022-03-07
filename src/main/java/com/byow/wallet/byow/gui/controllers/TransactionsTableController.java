package com.byow.wallet.byow.gui.controllers;

import com.byow.wallet.byow.observables.CurrentWallet;
import com.byow.wallet.byow.observables.TransactionRow;
import javafx.collections.ListChangeListener;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Comparator;

import static com.byow.wallet.byow.utils.Copy.copy;

@Component
public class TransactionsTableController extends TableView<TransactionRow> {
    @FXML
    public TableView<TransactionRow> transactionsTable;

    @FXML
    public TableColumn<TransactionRow, String> columnTransactionId;

    @FXML
    public TableColumn<TransactionRow, BigDecimal> columnTransactionBalance;

    @FXML
    public TableColumn<TransactionRow, Long> columnTransactionConfirmations;

    @FXML
    public TableColumn<TransactionRow, String> columnTransactionDate;

    private final CurrentWallet currentWallet;

    public TransactionsTableController(
        @Value("fxml/transactions_table.fxml") Resource fxml,
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
        transactionsTable.setItems(
            new SortedList<>(
                currentWallet.getObservableTransactionRows(),
                Comparator.comparing((TransactionRow t) -> Instant.parse(t.getDate()), Comparator.reverseOrder())
            )
        );
        currentWallet.getObservableTransactionRows().addListener((ListChangeListener<TransactionRow>) c -> transactionsTable.refresh());
        columnTransactionId.setCellValueFactory(new PropertyValueFactory<>("id"));
        columnTransactionBalance.setCellValueFactory(new PropertyValueFactory<>("balance"));
        columnTransactionConfirmations.setCellValueFactory(new PropertyValueFactory<>("confirmations"));
        columnTransactionDate.setCellValueFactory(new PropertyValueFactory<>("date"));
    }

    public void copyTransactionId() {
        TransactionRow item = transactionsTable.getSelectionModel().getSelectedItem();
        copy(item.getId());
    }

}
