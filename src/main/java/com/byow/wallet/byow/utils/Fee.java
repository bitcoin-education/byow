package com.byow.wallet.byow.utils;

import com.byow.wallet.byow.domains.Utxo;
import io.github.bitcoineducation.bitcoinjava.Transaction;
import io.github.bitcoineducation.bitcoinjava.TransactionOutput;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

public class Fee {
    public static BigInteger totalCalculatedFee(Transaction transaction, BigDecimal feeRate) {
        try {
            BigInteger feeRateInSatoshisPerVByte = Satoshi.btcPerKbToSatoshiPerByte(feeRate);
            return feeRateInSatoshisPerVByte.multiply(BigInteger.valueOf(transaction.getVSize()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static BigInteger totalActualFee(Transaction transaction, List<Utxo> selectedUtxos) {
        BigDecimal inputSumInBtcs = selectedUtxos.stream()
            .map(Utxo::amount)
            .reduce(BigDecimal::add)
            .orElse(BigDecimal.ZERO);
        BigInteger outputSum = transaction.getOutputs()
            .stream()
            .map(TransactionOutput::getAmount)
            .reduce(BigInteger::add)
            .orElse(BigInteger.ZERO);
        return Satoshi.toSatoshis(inputSumInBtcs).subtract(outputSum);
    }
}
