package com.byow.wallet.byow.gui.services;

import com.byow.wallet.byow.api.services.node.NodeUTXOService;
import com.byow.wallet.byow.domains.Utxo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UpdateUTXOsService {
    private final NodeUTXOService nodeUtxoService;

    public UpdateUTXOsService(NodeUTXOService nodeUtxoService) {
        this.nodeUtxoService = nodeUtxoService;
    }

    public void update(List<String> addresses, String name) {
        List<Utxo> utxos = nodeUtxoService.getUtxos(addresses, name);
        utxos.forEach(System.out::println);
    }
}
