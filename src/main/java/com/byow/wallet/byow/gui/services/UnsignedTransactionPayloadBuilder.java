package com.byow.wallet.byow.gui.services;

import com.byow.wallet.byow.domains.TransactionDto;
import com.byow.wallet.byow.domains.UnsignedTransactionPayload;
import com.byow.wallet.byow.domains.UtxoDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UnsignedTransactionPayloadBuilder {
    private final UtxoDtoBuilder utxoDtoBuilder;

    private final ObjectMapper objectMapper;

    public UnsignedTransactionPayloadBuilder(UtxoDtoBuilder utxoDtoBuilder, ObjectMapper objectMapper) {
        this.utxoDtoBuilder = utxoDtoBuilder;
        this.objectMapper = objectMapper;
    }

    public String build(TransactionDto transactionDto) {
        List<UtxoDto> utxoDtos = transactionDto.selectedUtxos().stream()
            .map(utxoDtoBuilder::build)
            .toList();
        UnsignedTransactionPayload unsignedTransactionPayload = new UnsignedTransactionPayload(utxoDtos, transactionDto.transaction());
        try {
            return objectMapper.writeValueAsString(unsignedTransactionPayload);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
