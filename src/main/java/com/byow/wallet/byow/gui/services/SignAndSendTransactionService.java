package com.byow.wallet.byow.gui.services;

import com.byow.wallet.byow.api.services.TransactionSignerService;
import com.byow.wallet.byow.api.services.node.client.NodeSendRawTransactionClient;
import com.byow.wallet.byow.domains.AddressConfig;
import com.byow.wallet.byow.domains.AddressType;
import com.byow.wallet.byow.domains.Utxo;
import com.byow.wallet.byow.domains.UtxoDto;
import com.byow.wallet.byow.observables.CurrentWallet;
import io.github.bitcoineducation.bitcoinjava.Transaction;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class SignAndSendTransactionService {
    private final CurrentWallet currentWallet;

    private final TransactionSignerService transactionSignerService;

    private final List<AddressConfig> addressConfigs;

    private final NodeSendRawTransactionClient nodeSendRawTransactionClient;

    public SignAndSendTransactionService(
        CurrentWallet currentWallet,
        TransactionSignerService transactionSignerService,
        List<AddressConfig> addressConfigs,
        NodeSendRawTransactionClient nodeSendRawTransactionClient
    ) {
        this.currentWallet = currentWallet;
        this.transactionSignerService = transactionSignerService;
        this.addressConfigs = addressConfigs;
        this.nodeSendRawTransactionClient = nodeSendRawTransactionClient;
    }

    public void signAndSend(Transaction transaction, String password, List<Utxo> utxos) {
        List<UtxoDto> utxoDtos = utxos.stream()
            .map(this::buildUtxoDto)
            .toList();
        transactionSignerService.sign(transaction, currentWallet.getMnemonicSeed(), password, utxoDtos);
        try {
            nodeSendRawTransactionClient.send(transaction.serialize());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private UtxoDto buildUtxoDto(Utxo utxo) {
        Long addressIndex = currentWallet.getAddress(utxo.address()).getIndex();
        AddressType addressType = currentWallet.getAddress(utxo.address()).getAddressType();
        String derivationPath = buildDerivationPath(addressType, addressIndex);
        return new UtxoDto(derivationPath, utxo.amount());
    }

    private String buildDerivationPath(AddressType addressType, Long addressIndex) {
        return addressConfigs.stream()
            .filter(addressConfig -> addressConfig.addressType().equals(addressType))
            .findFirst()
            .get()
            .derivationPath()
            .concat("/".concat(addressIndex.toString()));
    }
}
