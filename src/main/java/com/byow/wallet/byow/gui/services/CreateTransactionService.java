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
import com.byow.wallet.byow.utils.Fee;
import com.byow.wallet.byow.utils.Satoshi;
import io.github.bitcoineducation.bitcoinjava.Transaction;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

@Service
public class CreateTransactionService {
    private final EstimateFeeService estimateFeeService;

    private final CurrentWallet currentWallet;

    private final NodeListUnspentClient nodeListUnspentClient;

    private final CoinSelector coinSelector;

    private final TransactionCreatorService transactionCreatorService;

    public CreateTransactionService(
        EstimateFeeService estimateFeeService,
        CurrentWallet currentWallet,
        NodeListUnspentClient nodeListUnspentClient,
        CoinSelector coinSelector,
        TransactionCreatorService transactionCreatorService
    ) {
        this.estimateFeeService = estimateFeeService;
        this.currentWallet = currentWallet;
        this.nodeListUnspentClient = nodeListUnspentClient;
        this.coinSelector = coinSelector;
        this.transactionCreatorService = transactionCreatorService;
    }

    public TransactionDto create(String address, BigDecimal amount) {
        BigDecimal feeRate = estimateFeeService.estimate();

        List<String> addresses = currentWallet.getAddressesAsStrings();

        if (addresses.isEmpty()) {
            throw new CreateTransactionException(ErrorMessages.WALLET_NOT_LOADED);
        }

        List<Utxo> utxos = nodeListUnspentClient.listUnspent(addresses, currentWallet.getName());

        List<Utxo> selectedUtxos = coinSelector.select(utxos, Satoshi.toSatoshis(amount), feeRate, address, currentWallet.getChangeAddress());
        Transaction transaction = transactionCreatorService.create(
            selectedUtxos,
            address,
            Satoshi.toSatoshis(amount),
            currentWallet.getChangeAddress(),
            feeRate
        );

        BigInteger totalActualFee = Fee.totalActualFee(transaction, selectedUtxos);
        BigInteger totalCalculatedFee = Fee.totalCalculatedFee(transaction, feeRate);
        BigInteger totalSpent = totalSpent(transaction, totalActualFee, address);
        TransactionDto transactionDto = new TransactionDto(
            transaction,
            feeRate,
            amount,
            Satoshi.toBtc(totalActualFee),
            Satoshi.toBtc(totalCalculatedFee),
            Satoshi.toBtc(totalSpent),
            address,
            selectedUtxos
        );
        validateFunds(transactionDto);
        return transactionDto;
    }

    private void validateFunds(TransactionDto transactionDto) {
        BigDecimal inputSum = transactionDto.getInputAmountSum();
        BigDecimal outputSum = transactionDto.getOutputAmountSum();

        if (inputSum.compareTo(outputSum.add(transactionDto.totalCalculatedFee())) < 0) {
            throw new CreateTransactionException(ErrorMessages.NOT_ENOUGH_FUNDS);
        }
    }

    private BigInteger totalSpent(Transaction transaction, BigInteger totalActualFee, String address) {
        if (!currentWallet.getAddressesAsStrings().contains(address)) {
            return totalActualFee.add(transaction.getOutputs().get(0).getAmount());
        }
        return totalActualFee;
    }
}
