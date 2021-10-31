package com.byow.wallet.byow.node.listeners;

import com.byow.wallet.byow.node.events.TransactionReceivedEvent;
import io.github.bitcoineducation.bitcoinjava.PublicKey;
import io.github.bitcoineducation.bitcoinjava.Transaction;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class TransactionReceivedListener implements ApplicationListener<TransactionReceivedEvent> {
    @Override
    public void onApplicationEvent(TransactionReceivedEvent event) {
        Transaction transaction = event.getTransaction();
//        transaction.getInputs()
//            .stream()
//            .filter(transactionInput -> !transactionInput.getWitness().getItems().isEmpty())
//            .map(transactionInput -> (String) transactionInput.getWitness().getItems().get(1))
//            .map(publicKeyHex -> PublicKey.fromCompressedPublicKey(Hex.decodeStrict(publicKeyHex)).segwitAddressFromCompressedPublicKey())
    }
}
