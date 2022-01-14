package com.byow.wallet.byow.gui.services;

import com.byow.wallet.byow.api.services.node.NodeLoadOrCreateWalletService;
import com.byow.wallet.byow.api.services.node.client.NodeMultiImportAddressClient;
import com.byow.wallet.byow.domains.Wallet;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.Future;

import static java.lang.Thread.currentThread;

@Service
public class ImportWalletService {
    private final NodeLoadOrCreateWalletService nodeLoadOrCreateWalletService;

    private final NodeMultiImportAddressClient nodeMultiImportAddressClient;

    public ImportWalletService(NodeLoadOrCreateWalletService nodeLoadOrCreateWalletService, NodeMultiImportAddressClient nodeMultiImportAddressClient) {
        this.nodeLoadOrCreateWalletService = nodeLoadOrCreateWalletService;
        this.nodeMultiImportAddressClient = nodeMultiImportAddressClient;
    }

    @Async("defaultExecutorService")
    @Retryable(exceptionExpression = "@importWalletService.shouldRetry()", maxAttempts = Integer.MAX_VALUE, backoff = @Backoff(delay = 1000))
    public Future<Void> importWallet(Wallet wallet) {
        nodeLoadOrCreateWalletService.loadOrCreateWallet(wallet.name());
        nodeMultiImportAddressClient.importAddresses(wallet.name(), wallet.getAddresses(), wallet.createdAt());
        return new AsyncResult<>(null);
    }

    public boolean shouldRetry() {
        return !currentThread().isInterrupted();
    }
}
