package com.byow.wallet.byow.gui.services;

import com.byow.wallet.byow.api.services.TransactionSignerService;
import com.byow.wallet.byow.api.services.node.client.NodeSendRawTransactionClient;
import com.byow.wallet.byow.domains.*;
import com.byow.wallet.byow.gui.events.TransactionSentEvent;
import com.byow.wallet.byow.observables.CurrentWallet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Future;

@Service
public class SendTransactionService {
    private static final Logger logger = LoggerFactory.getLogger(SendTransactionService.class);

    private final CurrentWallet currentWallet;

    private final TransactionSignerService transactionSignerService;

    private final List<AddressConfig> addressConfigs;

    private final NodeSendRawTransactionClient nodeSendRawTransactionClient;

    private final ApplicationEventPublisher applicationEventPublisher;

    public SendTransactionService(
        CurrentWallet currentWallet,
        TransactionSignerService transactionSignerService,
        List<AddressConfig> addressConfigs,
        NodeSendRawTransactionClient nodeSendRawTransactionClient,
        ApplicationEventPublisher applicationEventPublisher
    ) {
        this.currentWallet = currentWallet;
        this.transactionSignerService = transactionSignerService;
        this.addressConfigs = addressConfigs;
        this.nodeSendRawTransactionClient = nodeSendRawTransactionClient;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Async("defaultExecutorService")
    public Future<Void> signAndSend(TransactionDto transactionDto, String password) {
        List<UtxoDto> utxoDtos = transactionDto.selectedUtxos().stream()
            .map(this::buildUtxoDto)
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


    private UtxoDto buildUtxoDto(Utxo utxo) {
        Long addressIndex = currentWallet.getAddress(utxo.address()).getIndex();
        AddressType addressType = currentWallet.getAddress(utxo.address()).getAddressType();
        String derivationPath = buildDerivationPath(addressType, addressIndex);
        return new UtxoDto(derivationPath, utxo.amount());
    }

    private String buildDerivationPath(AddressType addressType, Long addressIndex) {
        return addressConfigs.stream()
            .filter(addressConfig -> addressConfig.addressType().equals(addressType))
            .findFirst()
            .get()
            .derivationPath()
            .concat("/".concat(addressIndex.toString()));
    }
}
