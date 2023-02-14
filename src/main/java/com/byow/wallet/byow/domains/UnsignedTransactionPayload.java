package com.byow.wallet.byow.domains;

import com.byow.wallet.byow.utils.TransactionDeserializer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.github.bitcoineducation.bitcoinjava.Transaction;

import java.io.IOException;
import java.util.List;

public record UnsignedTransactionPayload(
    List<UtxoDto> utxos,
    @JsonDeserialize(using = TransactionDeserializer.class) Transaction transaction
) {

    @JsonProperty("transaction")
    public String transactionSerializer() {
        try {
            return transaction.serialize();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
