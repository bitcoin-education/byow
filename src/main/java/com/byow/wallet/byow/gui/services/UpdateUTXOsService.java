package com.byow.wallet.byow.gui.services;

import com.byow.wallet.byow.api.services.node.NodeUTXOService;
import com.byow.wallet.byow.domains.Utxo;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UpdateUTXOsService {
    private final NodeUTXOService nodeUtxoService;

    private final UpdateCurrentWalletAddressesService updateCurrentWalletAddressesService;

    public UpdateUTXOsService(
        NodeUTXOService nodeUtxoService,
        UpdateCurrentWalletAddressesService updateCurrentWalletAddressesService
    ) {
        this.nodeUtxoService = nodeUtxoService;
        this.updateCurrentWalletAddressesService = updateCurrentWalletAddressesService;
    }

    @Async("defaultExecutorService")
    public void update(List<String> addresses, String name) {
        List<Utxo> utxos = nodeUtxoService.getUtxos(addresses, name);
        updateCurrentWalletAddressesService.update(utxos);
    }
}
