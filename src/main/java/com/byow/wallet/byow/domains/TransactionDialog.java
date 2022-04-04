package com.byow.wallet.byow.domains;

import java.math.BigDecimal;

import static com.byow.wallet.byow.utils.BitcoinFormatter.format;

public record TransactionDialog(
    String amountToSend,
    String totalFee,
    String total,
    BigDecimal feeRate,
    String addressToSend
) {
    public static TransactionDialog from(TransactionDto transactionDto) {
        return new TransactionDialog(
            format(transactionDto.amountToSend()),
            format(transactionDto.totalFee()),
            format(transactionDto.totalSpent()),
            transactionDto.feeRate(),
            transactionDto.address()
        );
    }
}
