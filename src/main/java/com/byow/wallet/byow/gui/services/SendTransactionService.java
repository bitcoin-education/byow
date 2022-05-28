package com.byow.wallet.byow.gui.services;

import com.byow.wallet.byow.api.services.TransactionSignerService;
import com.byow.wallet.byow.api.services.node.client.NodeSendRawTransactionClient;
import com.byow.wallet.byow.domains.TransactionDto;
import com.byow.wallet.byow.domains.UtxoDto;
import com.byow.wallet.byow.domains.node.Error;
import com.byow.wallet.byow.gui.events.TransactionSentEvent;
import com.byow.wallet.byow.observables.CurrentWallet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Future;

@Service
public class SendTransactionService {
    private static final Logger logger = LoggerFactory.getLogger(SendTransactionService.class);

    private final UtxoDtoBuilder utxoDtoBuilder;

    private final TransactionSignerService transactionSignerService;

    private final CurrentWallet currentWallet;

    private final NodeSendRawTransactionClient nodeSendRawTransactionClient;

    private final ApplicationEventPublisher applicationEventPublisher;

    private final NodeErrorHandler nodeErrorHandler;

    public SendTransactionService(
        UtxoDtoBuilder utxoDtoBuilder,
        TransactionSignerService transactionSignerService,
        CurrentWallet currentWallet,
        NodeSendRawTransactionClient nodeSendRawTransactionClient,
        ApplicationEventPublisher applicationEventPublisher,
        NodeErrorHandler nodeErrorHandler
    ) {
        this.utxoDtoBuilder = utxoDtoBuilder;
        this.transactionSignerService = transactionSignerService;
        this.currentWallet = currentWallet;
        this.nodeSendRawTransactionClient = nodeSendRawTransactionClient;
        this.applicationEventPublisher = applicationEventPublisher;
        this.nodeErrorHandler = nodeErrorHandler;
    }

    @Async("defaultExecutorService")
    public Future<Error> signAndSend(TransactionDto transactionDto, String password) {
        List<UtxoDto> utxoDtos = transactionDto.selectedUtxos().stream()
            .map(utxoDtoBuilder::build)
            .toList();
        transactionSignerService.sign(transactionDto.transaction(), currentWallet.getMnemonicSeed(), password, utxoDtos);
        try {
            nodeSendRawTransactionClient.send(transactionDto.transaction().serialize());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (HttpServerErrorException.InternalServerError error) {
            logger.error(error.getResponseBodyAsString());
            return nodeErrorHandler.handleError(error);
        }
        applicationEventPublisher.publishEvent(new TransactionSentEvent(this, transactionDto));
        return new AsyncResult<>(null);
    }
}
