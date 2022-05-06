package com.byow.wallet.byow.gui.events;

import com.byow.wallet.byow.domains.TransactionDto;
import org.springframework.context.ApplicationEvent;

public class TransactionSentEvent extends ApplicationEvent {
    private final TransactionDto transactionDto;

    public TransactionSentEvent(Object source, TransactionDto transactionDto) {
        super(source);
        this.transactionDto = transactionDto;
    }

    public TransactionDto getTransactionDto() {
        return transactionDto;
    }
}