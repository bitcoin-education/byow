package com.byow.wallet.byow.gui.controllers;

import com.byow.wallet.byow.domains.Address;
import com.byow.wallet.byow.observables.AddressRow;
import com.byow.wallet.byow.observables.CurrentWallet;
import javafx.collections.ListChangeListener;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;

@Component
public class AddressesTableController extends TableView<Address> {
    @FXML
    public TableView<AddressRow> addressesTable;

    @FXML
    public TableColumn<AddressRow, String> columnAddress;

    @FXML
    public TableColumn<AddressRow, BigDecimal> columnBalance;

    @FXML
    public TableColumn<AddressRow, Long> columnConfirmations;

    private final CurrentWallet currentWallet;

    public AddressesTableController(
        @Value("fxml/addresses_table.fxml") Resource fxml,
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
        addressesTable.setItems(
            new FilteredList<>(
                currentWallet.getObservableAddressRows(),
                addressRow -> new BigDecimal(addressRow.getBalance()).doubleValue() > 0
            )
        );
        currentWallet.getObservableAddressRows().addListener((ListChangeListener<AddressRow>) c -> addressesTable.refresh());
        columnAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        columnBalance.setCellValueFactory(new PropertyValueFactory<>("balance"));
        columnConfirmations.setCellValueFactory(new PropertyValueFactory<>("confirmations"));
    }

    public void unfreezeAddress() {
        AddressRow selected = addressesTable.getSelectionModel().getSelectedItem();
        currentWallet.unfreeze(selected.getAddress());
    }

    public void freezeAddress() {
        AddressRow selected = addressesTable.getSelectionModel().getSelectedItem();
        currentWallet.freeze(selected.getAddress());
    }
}
