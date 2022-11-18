package com.byow.wallet.byow.api.services;

import com.byow.wallet.byow.domains.Utxo;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
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
        return null;
    }

}
