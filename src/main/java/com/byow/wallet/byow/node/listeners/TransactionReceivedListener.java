package com.byow.wallet.byow.node.listeners;

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

    public TransactionReceivedListener(AddressParser addressParser, CurrentWallet currentWallet) {
        this.addressParser = addressParser;
        this.currentWallet = currentWallet;
    }

    @Override
    public void onApplicationEvent(TransactionReceivedEvent event) {
        Transaction transaction = event.getTransaction();
        List<String> addresses = transaction.getOutputs()
            .stream()
            .map(addressParser::parse)
            .filter(address -> !address.isEmpty() && currentWallet.getAddressesAsStrings().contains(address))
            .toList();
    }
}
