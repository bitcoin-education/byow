package com.byow.wallet.byow.gui.services;

import com.byow.wallet.byow.api.services.TransactionSignerService;
import com.byow.wallet.byow.api.services.node.client.NodeSendRawTransactionClient;
import com.byow.wallet.byow.domains.*;
import com.byow.wallet.byow.gui.events.TransactionSentEvent;
import com.byow.wallet.byow.observables.CurrentWallet;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Future;

@Service
public class SendTransactionService {
    private final CurrentWallet currentWallet;

    private final TransactionSignerService transactionSignerService;

    private final NodeSendRawTransactionClient nodeSendRawTransactionClient;

    private final ApplicationEventPublisher applicationEventPublisher;

    private final UtxoDtoBuilder utxoDtoBuilder;

    public SendTransactionService(
        CurrentWallet currentWallet,
        TransactionSignerService transactionSignerService,
        NodeSendRawTransactionClient nodeSendRawTransactionClient,
        ApplicationEventPublisher applicationEventPublisher,
        UtxoDtoBuilder utxoDtoBuilder
    ) {
        this.currentWallet = currentWallet;
        this.transactionSignerService = transactionSignerService;
        this.nodeSendRawTransactionClient = nodeSendRawTransactionClient;
        this.applicationEventPublisher = applicationEventPublisher;
        this.utxoDtoBuilder = utxoDtoBuilder;
    }

    @Async("defaultExecutorService")
    public Future<Void> signAndSend(TransactionDto transactionDto, String password) {
        List<UtxoDto> utxoDtos = transactionDto.selectedUtxos().stream()
            .map(utxoDtoBuilder::build)
            .toList();
        transactionSignerService.sign(transactionDto.transaction(), currentWallet.getMnemonicSeed(), password, utxoDtos);
        try {
            nodeSendRawTransactionClient.send(transactionDto.transaction().serialize());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        applicationEventPublisher.publishEvent(new TransactionSentEvent(this, transactionDto));
        return new AsyncResult<>(null);
    }

}
