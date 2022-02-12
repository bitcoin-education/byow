package com.byow.wallet.byow.gui.services;

import com.byow.wallet.byow.api.services.node.client.NodeListUnspentClient;
import com.byow.wallet.byow.domains.Utxo;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UpdateUTXOsService {

    private final NodeListUnspentClient nodeListUnspentClient;

    private final UpdateCurrentWalletAddressesService updateCurrentWalletAddressesService;

    public UpdateUTXOsService(NodeListUnspentClient nodeListUnspentClient, UpdateCurrentWalletAddressesService updateCurrentWalletAddressesService) {
        this.nodeListUnspentClient = nodeListUnspentClient;
        this.updateCurrentWalletAddressesService = updateCurrentWalletAddressesService;
    }

    @Async("defaultExecutorService")
    public void update(List<String> addresses, String name) {
        List<Utxo> utxos = nodeListUnspentClient.listUnspent(addresses, name);
        updateCurrentWalletAddressesService.update(utxos);
    }
}
