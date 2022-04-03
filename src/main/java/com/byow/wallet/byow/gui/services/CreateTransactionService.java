package com.byow.wallet.byow.gui.services;

import com.byow.wallet.byow.api.services.CoinSelector;
import com.byow.wallet.byow.api.services.EstimateFeeService;
import com.byow.wallet.byow.api.services.TransactionCreatorService;
import com.byow.wallet.byow.api.services.node.client.NodeListUnspentClient;
import com.byow.wallet.byow.domains.TransactionDto;
import com.byow.wallet.byow.domains.Utxo;
import com.byow.wallet.byow.observables.CurrentWallet;
import com.byow.wallet.byow.utils.Satoshi;
import io.github.bitcoineducation.bitcoinjava.Transaction;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import static com.byow.wallet.byow.utils.Fee.totalFee;

@Service
public class CreateTransactionService {
    private final TransactionCreatorService transactionCreatorService;

    private final CurrentWallet currentWallet;

    private final NodeListUnspentClient nodeListUnspentClient;

    private final EstimateFeeService estimateFeeService;

    private final CoinSelector coinSelector;

    public CreateTransactionService(
        TransactionCreatorService transactionCreatorService,
        CurrentWallet currentWallet,
        NodeListUnspentClient nodeListUnspentClient,
        EstimateFeeService estimateFeeService,
        CoinSelector coinSelector
    ) {
        this.transactionCreatorService = transactionCreatorService;
        this.currentWallet = currentWallet;
        this.nodeListUnspentClient = nodeListUnspentClient;
        this.estimateFeeService = estimateFeeService;
        this.coinSelector = coinSelector;
    }

    public TransactionDto create(String address, BigDecimal amount) {
        BigDecimal feeRate = estimateFeeService.estimate();

        List<String> addresses = currentWallet.getAddressesAsStrings();
        if (addresses.isEmpty()) {
            throw new RuntimeException(); //melhorar
        }

        List<Utxo> utxos = nodeListUnspentClient.listUnspent(addresses, currentWallet.getName());
        if (utxos.isEmpty()) {
            throw new RuntimeException(); //melhorar
        }

        List<Utxo> selectedUtxos = coinSelector.select(utxos, Satoshi.toSatoshis(amount), feeRate, address, currentWallet.getChangeAddress());
        Transaction transaction = transactionCreatorService.create(
            selectedUtxos,
            address,
            Satoshi.toSatoshis(amount),
            currentWallet.getChangeAddress(),
            feeRate
        );

        BigInteger totalFee = totalFee(transaction, feeRate);
        BigInteger totalSpent = totalSpent(transaction, totalFee);
        return new TransactionDto(transaction, feeRate, amount, Satoshi.toBtc(totalFee), Satoshi.toBtc(totalSpent), address, selectedUtxos);
    }

    private BigInteger totalSpent(Transaction transaction, BigInteger totalFee) {
        return totalFee.add(transaction.getOutputs().get(0).getAmount());
    }

}
