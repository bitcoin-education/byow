package com.byow.wallet.byow.gui.services;

import com.byow.wallet.byow.api.services.AddAddressService;
import com.byow.wallet.byow.api.services.node.client.NodeMultiImportAddressClient;
import com.byow.wallet.byow.database.repositories.WalletRepository;
import com.byow.wallet.byow.domains.AddressType;
import com.byow.wallet.byow.domains.ExtendedPubkey;
import com.byow.wallet.byow.domains.node.NodeTransaction;
import com.byow.wallet.byow.observables.CurrentWallet;
import javafx.application.Platform;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class UpdateCurrentWalletReceivingAddressesService {

    private final CurrentWallet currentWallet;

    private final AddAddressService addAddressService;

    private final int initialNumberOfGeneratedAddresses;

    private final NodeMultiImportAddressClient nodeMultiImportAddressClient;

    private final WalletRepository walletRepository;

    public UpdateCurrentWalletReceivingAddressesService(
        CurrentWallet currentWallet,
        AddAddressService addAddressService,
        @Qualifier("initialNumberOfGeneratedAddresses") int initialNumberOfGeneratedAddresses,
        NodeMultiImportAddressClient nodeMultiImportAddressClient,
        WalletRepository walletRepository
    ) {
        this.currentWallet = currentWallet;
        this.addAddressService = addAddressService;
        this.initialNumberOfGeneratedAddresses = initialNumberOfGeneratedAddresses;
        this.nodeMultiImportAddressClient = nodeMultiImportAddressClient;
        this.walletRepository = walletRepository;
    }

    public void update(List<NodeTransaction> nodeTransactions) {
        nodeTransactions.stream()
            .map(NodeTransaction::address)
            .filter(address -> currentWallet.getAddressesAsStrings().contains(address))
            .forEach(this::markAsUsed);
        currentWallet.getAddressTypes().forEach(this::updateReceivingAddress);
    }

    private void updateReceivingAddress(AddressType addressType) {
        long nextAddressIndex = currentWallet.findNextAddressIndex(addressType);
        if (mustImportAddresses(nextAddressIndex, addressType)) {
            List<ExtendedPubkey> extendedPubkeys = currentWallet.getExtendedPubkeys();
            addAddressService.addAddresses(extendedPubkeys, nextAddressIndex, initialNumberOfGeneratedAddresses);
            currentWallet.setAddresses(extendedPubkeys);
            List<String> addressStrings = currentWallet.getAddressesAsStrings(nextAddressIndex, nextAddressIndex + initialNumberOfGeneratedAddresses);
            nodeMultiImportAddressClient.importAddresses(currentWallet.getFirstAddress(), addressStrings, new Date());
            walletRepository.incrementNumberOfGeneratedAddresses(initialNumberOfGeneratedAddresses, currentWallet.getName());
        }
        Platform.runLater(() -> currentWallet.setReceivingAddress(nextAddressIndex, addressType));
    }

    private boolean mustImportAddresses(long nextAddressIndex, AddressType addressType) {
        return nextAddressIndex == currentWallet.getAddressesCount(addressType);
    }

    private void markAsUsed(String address) {
        currentWallet.markAddressAsUsed(address);
    }

}
