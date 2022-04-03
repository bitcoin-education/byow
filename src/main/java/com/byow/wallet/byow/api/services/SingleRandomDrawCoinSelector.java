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

    public SingleRandomDrawCoinSelector(TransactionSizeCalculator transactionSizeCalculator) {
        this.transactionSizeCalculator = transactionSizeCalculator;
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
            if(totalInputBalance == adjustedTarget.longValue()) {
                break;
            }

            outputAddresses.add(changeAddress);
            totalFee = totalFee(feeRateInSatoshisPerVByte, inputAddresses, outputAddresses);
            adjustedTarget = amountToSend.add(totalFee);
            if(totalInputBalance >= adjustedTarget.longValue()) {
                break;
            }
        }

        return selectedUtxos;
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
