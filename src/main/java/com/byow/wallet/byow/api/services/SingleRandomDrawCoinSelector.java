package com.byow.wallet.byow.api.services;

import com.byow.wallet.byow.domains.Utxo;
import com.byow.wallet.byow.utils.Satoshi;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
        BigInteger feeRateInSatoshisPerVByte = Satoshi.btcPerKbToSatoshiPerByte(feeRate);
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

            if (inputBalanceFulfilledTransaction(totalInputBalance, adjustedTarget, adjustedTargetWithChange, changeAddress)) {
                break;
            }
        }

        return selectedUtxos;
    }

    private boolean inputBalanceFulfilledTransaction(long totalInputBalance, BigInteger adjustedTarget, BigInteger adjustedTargetWithChange, String changeAddress) {
        return totalInputBalance >= adjustedTarget.longValue()
            && changeIsDust(totalInputBalance, adjustedTargetWithChange, changeAddress)
            || totalInputBalance >= adjustedTargetWithChange.longValue();
    }

    private boolean changeIsDust(long totalInputBalance, BigInteger adjustedTargetWithChange, String changeAddress) {
        return dustCalculator.isDust(BigInteger.valueOf(totalInputBalance - adjustedTargetWithChange.longValue()), changeAddress);
    }

    private BigInteger totalFee(BigInteger feeRateInSatoshisPerVByte, List<String> inputAddresses, ArrayList<String> outputAddresses) {
        return transactionSizeCalculator.calculate(inputAddresses, outputAddresses).multiply(feeRateInSatoshisPerVByte);
    }

    private List<Utxo> shuffledCoins(List<Utxo> utxos) {
        List<Utxo> shuffledCoins = new ArrayList<>(utxos);
        Collections.shuffle(shuffledCoins);
        return shuffledCoins;
    }

    private List<Utxo> filterConfirmedUtxos(List<Utxo> utxos) {
        return utxos.stream()
            .filter(utxo -> utxo.confirmations() > 0)
            .toList();
    }
}
