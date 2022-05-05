package com.byow.wallet.byow.domains;

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
}
