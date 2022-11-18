package com.byow.wallet.byow.api.services;

import com.byow.wallet.byow.domains.Utxo;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

public interface CoinSelector {
    List<Utxo> select(List<Utxo> utxos, BigInteger amountToSend, BigDecimal feeRate, String addressToSend, String changeAddress);
}
