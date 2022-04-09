package com.byow.wallet.byow.gui.services;

import com.byow.wallet.byow.api.services.CoinSelector;
import com.byow.wallet.byow.api.services.EstimateFeeService;
import com.byow.wallet.byow.api.services.TransactionCreatorService;
import com.byow.wallet.byow.api.services.node.client.NodeListUnspentClient;
import com.byow.wallet.byow.domains.TransactionDto;
import com.byow.wallet.byow.domains.Utxo;
import com.byow.wallet.byow.domains.node.ErrorMessages;
import com.byow.wallet.byow.gui.exceptions.CreateTransactionException;
import com.byow.wallet.byow.observables.CurrentWallet;
import com.byow.wallet.byow.utils.Satoshi;
import io.github.bitcoineducation.bitcoinjava.Transaction;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import static com.byow.wallet.byow.utils.Fee.totalActualFee;
import static com.byow.wallet.byow.utils.Fee.totalCalculatedFee;

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
            throw new CreateTransactionException(ErrorMessages.WALLET_NOT_LOADED);
        }

        List<Utxo> utxos = nodeListUnspentClient.listUnspent(addresses, currentWallet.getName());
        if (utxos.isEmpty()) {
            throw new CreateTransactionException(ErrorMessages.NOT_ENOUGH_FUNDS);
        }

        List<Utxo> selectedUtxos = coinSelector.select(utxos, Satoshi.toSatoshis(amount), feeRate, address, currentWallet.getChangeAddress());
        Transaction transaction = transactionCreatorService.create(
            selectedUtxos,
            address,
            Satoshi.toSatoshis(amount),
            currentWallet.getChangeAddress(),
            feeRate
        );

        BigInteger totalActualFee = totalActualFee(transaction, selectedUtxos);
        BigInteger totalCalculatedFee = totalCalculatedFee(transaction, feeRate);
        BigInteger totalSpent = totalSpent(transaction, totalActualFee, address);
        return new TransactionDto(
            transaction,
            feeRate,
            amount,
            Satoshi.toBtc(totalActualFee),
            Satoshi.toBtc(totalCalculatedFee),
            Satoshi.toBtc(totalSpent),
            address,
            selectedUtxos
        );
    }

    private BigInteger totalSpent(Transaction transaction, BigInteger totalFee, String address) {
        if (!currentWallet.getAddressesAsStrings().contains(address)) {
            return totalFee.add(transaction.getOutputs().get(0).getAmount());
        }
        return totalFee;
    }

}
