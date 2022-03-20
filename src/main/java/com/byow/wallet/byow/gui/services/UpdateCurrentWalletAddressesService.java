package com.byow.wallet.byow.gui.services;

import com.byow.wallet.byow.api.services.AddAddressService;
import com.byow.wallet.byow.api.services.node.client.NodeMultiImportAddressClient;
import com.byow.wallet.byow.domains.AddressType;
import com.byow.wallet.byow.domains.ExtendedPubkey;
import com.byow.wallet.byow.domains.Utxo;
import com.byow.wallet.byow.observables.CurrentWallet;
import javafx.application.Platform;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;

@Service
public class UpdateCurrentWalletAddressesService {

    private static final AddressType MAIN_ADDRESS = AddressType.SEGWIT;

    private final CurrentWallet currentWallet;

    private final AddAddressService addAddressService;

    private final int initialNumberOfGeneratedAddresses;

    private final NodeMultiImportAddressClient nodeMultiImportAddressClient;

    public UpdateCurrentWalletAddressesService(
        CurrentWallet currentWallet,
        AddAddressService addAddressService,
        @Qualifier("initialNumberOfGeneratedAddresses") int initialNumberOfGeneratedAddresses,
        NodeMultiImportAddressClient nodeMultiImportAddressClient
    ) {
        this.currentWallet = currentWallet;
        this.addAddressService = addAddressService;
        this.initialNumberOfGeneratedAddresses = initialNumberOfGeneratedAddresses;
        this.nodeMultiImportAddressClient = nodeMultiImportAddressClient;
    }

    public void update(List<Utxo> utxos) {
        Map<String, List<Utxo>> groupedUtxos = utxos.stream().collect(groupingBy(Utxo::address));
        groupedUtxos.forEach((address, utxoList) -> {
            setBalance(address, utxoList);
            setConfirmations(address, utxoList);
            markAsUsed(address);
            Platform.runLater(() -> currentWallet.setAddressRow(address));
            updateReceivingAddress(address);
        });
    }

    private void updateReceivingAddress(String address) {
        AddressType addressType = currentWallet.getAddressType(address);
        long nextAddressIndex = currentWallet.findNextAddressIndex(addressType);
        if (mustImportAddresses(nextAddressIndex, addressType)) {
            List<ExtendedPubkey> extendedPubkeys = currentWallet.getExtendedPubkeys();
            addAddressService.addAddresses(extendedPubkeys, nextAddressIndex);
            currentWallet.setAddresses(extendedPubkeys);
            List<String> addressStrings = currentWallet.getAddressesAsStrings(nextAddressIndex, nextAddressIndex + initialNumberOfGeneratedAddresses);
            nodeMultiImportAddressClient.importAddresses(currentWallet.getName(), addressStrings, new Date());
        }
        if (addressType == MAIN_ADDRESS) {
            String nextAddress = currentWallet.getAddressAt(nextAddressIndex, addressType);
            Platform.runLater(() -> currentWallet.setReceivingAddress(nextAddress));
        }
    }

    private boolean mustImportAddresses(long nextAddressIndex, AddressType addressType) {
        return nextAddressIndex == currentWallet.getAddressesCount(addressType);
    }

    private void markAsUsed(String address) {
        currentWallet.markAddressAsUsed(address);
    }

    private void setConfirmations(String address, List<Utxo> utxoList) {
        long confirmations = utxoList.stream()
            .mapToLong(Utxo::confirmations)
            .min()
            .getAsLong();
        currentWallet.setAddressConfirmations(address, confirmations);
    }

    private void setBalance(String address, List<Utxo> utxoList) {
        double sum = utxoList.stream()
            .mapToDouble(Utxo::amount)
            .sum();
        currentWallet.setAddressBalance(address, sum);
    }
}
