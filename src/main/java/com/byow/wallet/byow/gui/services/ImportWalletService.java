package com.byow.wallet.byow.gui.services;

import com.byow.wallet.byow.api.services.node.NodeLoadOrCreateWalletService;
import com.byow.wallet.byow.api.services.node.client.NodeGetDescriptorInfoClient;
import com.byow.wallet.byow.api.services.node.client.NodeImportDescriptorsClient;
import com.byow.wallet.byow.api.services.node.client.NodeListDescriptorsClient;
import com.byow.wallet.byow.domains.Wallet;
import com.byow.wallet.byow.domains.node.NodeDescriptorInfoResponse;
import com.byow.wallet.byow.gui.annotations.ActivateProgressBar;
import com.byow.wallet.byow.utils.Descriptors;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Service
public class ImportWalletService {
    private final NodeLoadOrCreateWalletService nodeLoadOrCreateWalletService;

    private final NodeImportDescriptorsClient nodeImportDescriptorsClient;

    private final NodeGetDescriptorInfoClient nodeGetDescriptorInfoClient;

    private final NodeListDescriptorsClient nodeListDescriptorsClient;

    public ImportWalletService(
        NodeLoadOrCreateWalletService nodeLoadOrCreateWalletService,
        NodeImportDescriptorsClient nodeImportDescriptorsClient,
        NodeGetDescriptorInfoClient nodeGetDescriptorInfoClient,
        NodeListDescriptorsClient nodeListDescriptorsClient) {
        this.nodeLoadOrCreateWalletService = nodeLoadOrCreateWalletService;
        this.nodeImportDescriptorsClient = nodeImportDescriptorsClient;
        this.nodeGetDescriptorInfoClient = nodeGetDescriptorInfoClient;
        this.nodeListDescriptorsClient = nodeListDescriptorsClient;
    }

    @Async("defaultExecutorService")
    @ActivateProgressBar("Loading wallet...")
    @Retryable(exceptionExpression = "@importWalletService.shouldRetry()", maxAttempts = Integer.MAX_VALUE, backoff = @Backoff(delay = 1000))
    public Future<Void> importWallet(Wallet wallet) {
        nodeLoadOrCreateWalletService.loadOrCreateWallet(wallet.getFirstAddress(), true, true, "", false, true);
        List<String> notImportedDescriptors = getNotImportedDescriptors(wallet);
        if (!notImportedDescriptors.isEmpty()) {
            List<NodeDescriptorInfoResponse> descriptorsInfo = nodeGetDescriptorInfoClient.getDescriptorsInfo(wallet.getFirstAddress(), notImportedDescriptors);
            List<String> descriptors = descriptorsInfo.stream().map(NodeDescriptorInfoResponse::descriptor).toList();
            nodeImportDescriptorsClient.importDescriptors(wallet.getFirstAddress(), descriptors, wallet.createdAt());
        }
        return new AsyncResult<>(null);
    }

    private List<String> getNotImportedDescriptors(Wallet wallet) {
        List<String> currentDescriptors = Descriptors.getDescriptors(wallet.extendedPubkeys());
        Set<String> previouslyImportedDescriptors = nodeListDescriptorsClient
            .getDescriptors(wallet.getFirstAddress())
            .stream()
            .map(desc -> desc.split("#")[0])
            .collect(Collectors.toSet());
        return currentDescriptors.stream().dropWhile(previouslyImportedDescriptors::contains).toList();
    }
}
