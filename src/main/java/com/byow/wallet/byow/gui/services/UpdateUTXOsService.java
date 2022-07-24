package com.byow.wallet.byow.gui.services;

import com.byow.wallet.byow.api.services.node.client.NodeListTransactionsClient;
import com.byow.wallet.byow.api.services.node.client.NodeListUnspentClient;
import com.byow.wallet.byow.domains.TransactionDto;
import com.byow.wallet.byow.domains.Utxo;
import com.byow.wallet.byow.domains.node.NodeTransaction;
import com.byow.wallet.byow.gui.annotations.ActivateProgressBar;
import com.byow.wallet.byow.observables.TransactionRow;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class UpdateUTXOsService {

    private final NodeListUnspentClient nodeListUnspentClient;

    private final UpdateCurrentWalletAddressesService updateCurrentWalletAddressesService;

    private final UpdateCurrentWalletTransactionsService updateCurrentWalletTransactionsService;

    private final UpdateCurrentWalletBalanceService updateCurrentWalletBalanceService;

    private final NodeListTransactionsClient nodeListTransactionsClient;

    private final UpdateCurrentWalletReceivingAddressesService updateCurrentWalletReceivingAddressesService;

    public UpdateUTXOsService(
        NodeListUnspentClient nodeListUnspentClient,
        UpdateCurrentWalletAddressesService updateCurrentWalletAddressesService,
        UpdateCurrentWalletTransactionsService updateCurrentWalletTransactionsService,
        UpdateCurrentWalletBalanceService updateCurrentWalletBalanceService,
        NodeListTransactionsClient nodeListTransactionsClient,
        UpdateCurrentWalletReceivingAddressesService updateCurrentWalletReceivingAddressesService
    ) {
        this.nodeListUnspentClient = nodeListUnspentClient;
        this.updateCurrentWalletAddressesService = updateCurrentWalletAddressesService;
        this.updateCurrentWalletTransactionsService = updateCurrentWalletTransactionsService;
        this.updateCurrentWalletBalanceService = updateCurrentWalletBalanceService;
        this.nodeListTransactionsClient = nodeListTransactionsClient;
        this.updateCurrentWalletReceivingAddressesService = updateCurrentWalletReceivingAddressesService;
    }

    @Async("defaultExecutorService")
    @ActivateProgressBar("Updating UTXOs...")
    public void update(List<String> addresses, String name) {
        List<Utxo> utxos = nodeListUnspentClient.listUnspent(addresses, name);
        updateCurrentWalletAddressesService.update(utxos);
        List<NodeTransaction> nodeTransactions = nodeListTransactionsClient.listTransactions(name);
        updateCurrentWalletReceivingAddressesService.update(nodeTransactions);
        updateCurrentWalletTransactionsService.updateNodeTransactions(nodeTransactions);
        updateCurrentWalletBalanceService.update();
    }

    public void update(TransactionDto transactionDto) {
        List<Utxo> utxos = transactionDto.selectedUtxos().stream()
            .map(utxo -> new Utxo(
                utxo.txid(),
                utxo.vout(),
                utxo.address(),
                utxo.label(),
                utxo.scriptPubKey(),
                BigDecimal.ZERO,
                utxo.confirmations(),
                utxo.redeemScript(),
                utxo.witnessScript()
            )).toList();
        updateCurrentWalletAddressesService.update(utxos);
        updateCurrentWalletTransactionsService.update(TransactionRow.from(transactionDto));
        updateCurrentWalletBalanceService.update();
    }
}
