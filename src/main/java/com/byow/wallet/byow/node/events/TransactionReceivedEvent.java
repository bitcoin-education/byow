package com.byow.wallet.byow.node.events;

import com.byow.wallet.byow.node.NodeTask;
import io.github.bitcoineducation.bitcoinjava.Transaction;
import org.springframework.context.ApplicationEvent;

public class TransactionReceivedEvent extends ApplicationEvent {
    private final Transaction transaction;

    public TransactionReceivedEvent(NodeTask nodeTask, Transaction transaction) {
        super(nodeTask);
        this.transaction = transaction;
    }

    public Transaction getTransaction() {
        return transaction;
    }
}
