package com.byow.wallet.byow.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import io.github.bitcoineducation.bitcoinjava.Transaction;
import org.bouncycastle.util.encoders.Hex;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class TransactionDeserializer extends JsonDeserializer<Transaction> {
    @Override
    public Transaction deserialize(JsonParser jsonParser, DeserializationContext ctxt) {
        try {
            JsonNode node = jsonParser.getCodec().readTree(jsonParser);
            String transaction = node.asText();
            return deserializeTransaction(transaction);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Transaction deserializeTransaction(String transaction) {
        try {
            return Transaction.fromByteStream(new ByteArrayInputStream(Hex.decode(transaction)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
