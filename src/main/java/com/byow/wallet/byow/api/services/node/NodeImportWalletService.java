package com.byow.wallet.byow.api.services.node;

import com.byow.wallet.byow.domains.Address;
import com.byow.wallet.byow.domains.Wallet;
import com.byow.wallet.byow.domains.node.NodeWallets;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.Future;

import static java.lang.Thread.currentThread;

@Service
public class NodeImportWalletService {

    private final NodeWalletCreateService nodeWalletCreateService;

    private final NodeWalletAddressImportService nodeWalletAddressImportService;

    private final NodeListWalletsService nodeListWalletsService;

    private final NodeWalletLoadService nodeWalletLoadService;

    public NodeImportWalletService(
        NodeWalletCreateService nodeWalletCreateService,
        NodeWalletAddressImportService nodeWalletAddressImportService,
        NodeListWalletsService nodeListWalletsService,
        NodeWalletLoadService nodeWalletLoadService
    ) {
        this.nodeWalletCreateService = nodeWalletCreateService;
        this.nodeWalletAddressImportService = nodeWalletAddressImportService;
        this.nodeListWalletsService = nodeListWalletsService;
        this.nodeWalletLoadService = nodeWalletLoadService;
    }

    @Async("defaultExecutorService")
    @Retryable(exceptionExpression="@nodeImportWalletService.shouldRetry()", maxAttempts = Integer.MAX_VALUE, backoff = @Backoff(delay = 1000))
    public Future<Void> load(Wallet wallet) {
        createOrLoadWallet(wallet.name());
        nodeWalletAddressImportService.importAddresses(
            wallet.name(),
            getAddresses(wallet),
            wallet.createdAt()
        );
        return new AsyncResult<>(null);
    }

    public boolean shouldRetry() {
        return !currentThread().isInterrupted();
    }

    private void createOrLoadWallet(String name) {
        NodeWallets allWallets = nodeListWalletsService.listAll();
        List<String> loadedWallets = nodeListWalletsService.listLoaded();
        if(loadedWallets.stream().anyMatch(nodeWallet -> nodeWallet.equals(name))) {
            return;
        }
        if(allWallets.wallets().stream().anyMatch(nodeWallet -> nodeWallet.name().equals(name))) {
            nodeWalletLoadService.load(name);
            return;
        }
        nodeWalletCreateService.create(name);
    }

    //TODO: passar metodo para wallet
    private List<String> getAddresses(Wallet wallet) {
        return wallet.extendedPubkeys()
            .stream()
            .flatMap(extendedPubkey -> extendedPubkey.getAddresses().stream())
            .map(Address::address)
            .toList();
    }
}
