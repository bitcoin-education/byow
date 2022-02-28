package com.byow.wallet.byow.observables;

import com.sun.javafx.collections.ObservableListWrapper;
import com.sun.javafx.collections.ObservableMapWrapper;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

public class TransactionRows {
    private final ObservableMap<String, TransactionRow> transactionRowMap = new ObservableMapWrapper<>(new LinkedHashMap<>());

    private final ObservableList<TransactionRow> transactionRowList = new ObservableListWrapper<>(new LinkedList<>());

    public TransactionRows() {
        MapChangeListener<String, TransactionRow> listener = change -> {
            if (change.wasRemoved()) {
                transactionRowList.remove(change.getValueRemoved());
            }
            if (change.wasAdded()) {
                transactionRowList.add(change.getValueAdded());
            }
        };
        transactionRowMap.addListener(listener);
    }

    public void addTransactionRows(List<TransactionRow> transactionRows) {
        transactionRows.forEach(transactionRow -> {
            if (transactionRowMap.containsKey(transactionRow.getId())) {
                transactionRow.setDate(transactionRowMap.get(transactionRow.getId()).getDate());
            }
            transactionRowMap.remove(transactionRow.getId());
            transactionRowMap.put(transactionRow.getId(), transactionRow);
        });
    }

    public ObservableList<TransactionRow> getObservableTransactionRowList() {
        return transactionRowList;
    }

    public void clear() {
        transactionRowMap.clear();
        transactionRowList.clear();
    }
}
