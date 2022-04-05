package com.byow.wallet.byow.gui.services;

import com.byow.wallet.byow.api.services.node.client.NodeListTransactionsClient;
import com.byow.wallet.byow.api.services.node.client.NodeListUnspentClient;
import com.byow.wallet.byow.domains.Utxo;
import com.byow.wallet.byow.domains.node.NodeTransaction;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UpdateUTXOsService {

    private final NodeListUnspentClient nodeListUnspentClient;

    private final UpdateCurrentWalletAddressesService updateCurrentWalletAddressesService;

    private final UpdateCurrentWalletTransactionsService updateCurrentWalletTransactionsService;

    private final UpdateCurrentWalletBalanceService updateCurrentWalletBalanceService;

    private final NodeListTransactionsClient nodeListTransactionsClient;

    public UpdateUTXOsService(
        NodeListUnspentClient nodeListUnspentClient,
        UpdateCurrentWalletAddressesService updateCurrentWalletAddressesService,
        UpdateCurrentWalletTransactionsService updateCurrentWalletTransactionsService,
        UpdateCurrentWalletBalanceService updateCurrentWalletBalanceService,
        NodeListTransactionsClient nodeListTransactionsClient
    ) {
        this.nodeListUnspentClient = nodeListUnspentClient;
        this.updateCurrentWalletAddressesService = updateCurrentWalletAddressesService;
        this.updateCurrentWalletTransactionsService = updateCurrentWalletTransactionsService;
        this.updateCurrentWalletBalanceService = updateCurrentWalletBalanceService;
        this.nodeListTransactionsClient = nodeListTransactionsClient;
    }

    @Async("defaultExecutorService")
    public void update(List<String> addresses, String name) {
        List<Utxo> utxos = nodeListUnspentClient.listUnspent(addresses, name);
        updateCurrentWalletAddressesService.update(utxos);
        List<NodeTransaction> nodeTransactions = nodeListTransactionsClient.listTransactions(name);
        updateCurrentWalletTransactionsService.updateNodeTransactions(nodeTransactions);
        updateCurrentWalletBalanceService.update();
    }
}
