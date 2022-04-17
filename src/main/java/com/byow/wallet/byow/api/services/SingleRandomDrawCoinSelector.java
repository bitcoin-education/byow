package com.byow.wallet.byow.api.services;

import com.byow.wallet.byow.domains.Utxo;
import com.byow.wallet.byow.utils.Satoshi;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static com.byow.wallet.byow.utils.Satoshi.btcPerKbToSatoshiPerByte;
import static java.util.Collections.shuffle;

@Service
public class SingleRandomDrawCoinSelector implements CoinSelector {

    private final TransactionSizeCalculator transactionSizeCalculator;

    private final DustCalculator dustCalculator;

    public SingleRandomDrawCoinSelector(TransactionSizeCalculator transactionSizeCalculator, DustCalculator dustCalculator) {
        this.transactionSizeCalculator = transactionSizeCalculator;
        this.dustCalculator = dustCalculator;
    }

    @Override
    public List<Utxo> select(List<Utxo> utxos, BigInteger amountToSend, BigDecimal feeRate, String addressToSend, String changeAddress) {
        utxos = filterConfirmedUtxos(utxos);
        BigInteger feeRateInSatoshisPerVByte = btcPerKbToSatoshiPerByte(feeRate);
        List<Utxo> shuffledCoins = shuffledCoins(utxos);

        ArrayList<Utxo> selectedUtxos = new ArrayList<>();
        long totalInputBalance = 0;
        for (Utxo utxo : shuffledCoins) {
            selectedUtxos.add(utxo);
            totalInputBalance += Satoshi.toSatoshis(utxo.amount()).longValue();
            if (totalInputBalance < amountToSend.longValue()) {
                continue;
            }

            ArrayList<String> outputAddresses = new ArrayList<>(List.of(addressToSend));
            List<String> inputAddresses = selectedUtxos.stream()
                .map(Utxo::address)
                .toList();
            BigInteger totalFee = totalFee(feeRateInSatoshisPerVByte, inputAddresses, outputAddresses);
            BigInteger adjustedTarget = amountToSend.add(totalFee);

            outputAddresses.add(changeAddress);
            BigInteger totalFeeWithChange = totalFee(feeRateInSatoshisPerVByte, inputAddresses, outputAddresses);
            BigInteger adjustedTargetWithChange = amountToSend.add(totalFeeWithChange);

            if (inputBalanceFulfilledTransaction(totalInputBalance, adjustedTarget, adjustedTargetWithChange)) {
                break;
            }

        }

        return selectedUtxos;
    }

    private boolean inputBalanceFulfilledTransaction(long totalInputBalance, BigInteger adjustedTarget, BigInteger adjustedTargetWithChange) {
        return totalInputBalance >= adjustedTarget.longValue()
            && changeIsDust(totalInputBalance, adjustedTargetWithChange)
            || totalInputBalance >= adjustedTargetWithChange.longValue();
    }

    private boolean changeIsDust(long totalInputBalance, BigInteger adjustedTarget) {
        return dustCalculator.isDust(BigInteger.valueOf(totalInputBalance - adjustedTarget.longValue()));
    }

    private BigInteger totalFee(BigInteger feeRateInSatoshisPerVByte, List<String> inputAddresses, List<String> outputAddresses) {
        return transactionSizeCalculator.calculate(inputAddresses, outputAddresses).multiply(feeRateInSatoshisPerVByte);
    }

    private List<Utxo> shuffledCoins(List<Utxo> utxos) {
        List<Utxo> shuffledCoins = new ArrayList<>(utxos);
        shuffle(shuffledCoins);
        return shuffledCoins;
    }

    private List<Utxo> filterConfirmedUtxos(List<Utxo> utxos) {
        return utxos.stream()
            .filter(utxo -> utxo.confirmations() > 0)
            .toList();
    }

}