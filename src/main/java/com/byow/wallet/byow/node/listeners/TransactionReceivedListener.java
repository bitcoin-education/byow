package com.byow.wallet.byow.node.listeners;

import com.byow.wallet.byow.node.events.TransactionReceivedEvent;
import com.byow.wallet.byow.node.services.AddressParser;
import io.github.bitcoineducation.bitcoinjava.Transaction;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TransactionReceivedListener implements ApplicationListener<TransactionReceivedEvent> {
    private final AddressParser addressParser;

    public TransactionReceivedListener(AddressParser addressParser) {
        this.addressParser = addressParser;
    }

    @Override
    public void onApplicationEvent(TransactionReceivedEvent event) {
        Transaction transaction = event.getTransaction();
        List<String> addresses = transaction.getOutputs()
            .stream()
            .map(addressParser::parse)
            .toList();
        addresses.forEach(System.out::println);
    }
}
