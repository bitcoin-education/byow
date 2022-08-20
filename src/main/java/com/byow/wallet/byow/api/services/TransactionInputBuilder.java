package com.byow.wallet.byow.api.services;

import com.byow.wallet.byow.domains.Utxo;
import io.github.bitcoineducation.bitcoinjava.TransactionInput;

public interface TransactionInputBuilder {
    TransactionInput build(Utxo utxo);
}
