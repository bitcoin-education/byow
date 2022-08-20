package com.byow.wallet.byow.domains;

import com.byow.wallet.byow.utils.Satoshi;
import io.github.bitcoineducation.bitcoinjava.Transaction;

import java.math.BigDecimal;
import java.util.List;

public record TransactionDto(
    Transaction transaction,
    BigDecimal feeRate,
    BigDecimal amountToSend,
    BigDecimal totalActualFee,
    BigDecimal totalCalculatedFee,
    BigDecimal totalSpent,
    String address,
    List<Utxo> selectedUtxos
) {
    public BigDecimal getInputAmountSum() {
        return selectedUtxos
            .stream()
            .map(Utxo::amount)
            .reduce(BigDecimal::add)
            .orElse(BigDecimal.ZERO);
    }

    public BigDecimal getOutputAmountSum() {
        return transaction
            .getOutputs()
            .stream()
            .map(transactionOutput -> Satoshi.toBtc(transactionOutput.getAmount()))
            .reduce(BigDecimal::add)
            .orElse(BigDecimal.ZERO);
    }
}
