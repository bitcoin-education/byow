package com.byow.wallet.byow.gui.listeners;

import com.byow.wallet.byow.gui.events.TransactionSentEvent;
import com.byow.wallet.byow.gui.services.UpdateUTXOsService;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class TransactionSentListener implements ApplicationListener<TransactionSentEvent> {
    private final UpdateUTXOsService updateUTXOsService;

    public TransactionSentListener(UpdateUTXOsService updateUTXOsService) {
        this.updateUTXOsService = updateUTXOsService;
    }

    @Override
    public void onApplicationEvent(TransactionSentEvent event) {
        updateUTXOsService.update(event.getTransactionDto());
    }
}