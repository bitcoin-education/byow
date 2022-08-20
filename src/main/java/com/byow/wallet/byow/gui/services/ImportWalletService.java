package com.byow.wallet.byow.gui.services;

import com.byow.wallet.byow.api.services.node.NodeLoadOrCreateWalletService;
import com.byow.wallet.byow.api.services.node.client.NodeMultiImportAddressClient;
import com.byow.wallet.byow.api.services.node.client.NodeReceivedByAddressClient;
import com.byow.wallet.byow.domains.Wallet;
import com.byow.wallet.byow.domains.node.NodeAddress;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import static java.lang.Thread.currentThread;

@Service
public class ImportWalletService {
    private final NodeLoadOrCreateWalletService nodeLoadOrCreateWalletService;

    private final NodeMultiImportAddressClient nodeMultiImportAddressClient;

    private final NodeReceivedByAddressClient nodeReceivedByAddressClient;

    public ImportWalletService(NodeLoadOrCreateWalletService nodeLoadOrCreateWalletService, NodeMultiImportAddressClient nodeMultiImportAddressClient, NodeReceivedByAddressClient nodeReceivedByAddressClient) {
        this.nodeLoadOrCreateWalletService = nodeLoadOrCreateWalletService;
        this.nodeMultiImportAddressClient = nodeMultiImportAddressClient;
        this.nodeReceivedByAddressClient = nodeReceivedByAddressClient;
    }

    @Async("defaultExecutorService")
    @Retryable(exceptionExpression = "@importWalletService.shouldRetry()", maxAttempts = Integer.MAX_VALUE, backoff = @Backoff(delay = 1000))
    public Future<Void> importWallet(Wallet wallet) {
        nodeLoadOrCreateWalletService.loadOrCreateWallet(wallet.getFirstAddress());
        if (!addressesImported(wallet)) {
            nodeMultiImportAddressClient.importAddresses(wallet.getFirstAddress(), wallet.getAddresses(), wallet.createdAt());
        }
        return new AsyncResult<>(null);
    }

    private boolean addressesImported(Wallet wallet) {
        Set<String> importedAddresses = nodeReceivedByAddressClient.listAddresses(wallet.getFirstAddress(), 0, true, true)
            .stream()
            .map(NodeAddress::address)
            .collect(Collectors.toSet());
        Set<String> walletAddresses = new HashSet<>(wallet.getAddresses());
        walletAddresses.removeAll(importedAddresses);
        return walletAddresses.isEmpty();
    }

    public boolean shouldRetry() {
        return !currentThread().isInterrupted();
    }
}
