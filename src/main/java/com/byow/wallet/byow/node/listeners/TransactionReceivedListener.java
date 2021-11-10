package com.byow.wallet.byow.node.listeners;

import com.byow.wallet.byow.gui.services.UpdateUTXOsService;
import com.byow.wallet.byow.node.events.TransactionReceivedEvent;
import com.byow.wallet.byow.node.services.AddressParser;
import com.byow.wallet.byow.observables.CurrentWallet;
import io.github.bitcoineducation.bitcoinjava.Transaction;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TransactionReceivedListener implements ApplicationListener<TransactionReceivedEvent> {
    private final AddressParser addressParser;

    private final CurrentWallet currentWallet;

    private final UpdateUTXOsService updateUTXOsService;

    public TransactionReceivedListener(
        AddressParser addressParser,
        CurrentWallet currentWallet,
        UpdateUTXOsService updateUTXOsService
    ) {
        this.addressParser = addressParser;
        this.currentWallet = currentWallet;
        this.updateUTXOsService = updateUTXOsService;
    }

    @Override
    public void onApplicationEvent(TransactionReceivedEvent event) {
        Transaction transaction = event.getTransaction();
        List<String> addresses = transaction.getOutputs()
            .stream()
            .map(addressParser::parse)
            .filter(address -> !address.isEmpty() && currentWallet.getAddressesAsStrings().contains(address))
            .toList();
        if (!addresses.isEmpty()) {
            updateUTXOsService.update(addresses, currentWallet.getName());
        }
    }
}