package com.byow.wallet.byow.utils;

import com.byow.wallet.byow.database.entities.ExtendedPubkeyEntity;
import com.byow.wallet.byow.domains.ExtendedPubkey;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ExtendedPubkeysSerializer {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String serialize(List<ExtendedPubkey> extendedPubkeys) {
        Map<String, String> extendedPubkeysMap = extendedPubkeys.stream()
            .collect(Collectors.toMap(ExtendedPubkey::getType, ExtendedPubkey::getKey));
        return serialize(extendedPubkeysMap);
    }

    public static List<ExtendedPubkey> unserialize(String extendedPubkeys) {
        try {
            Map<String, String> extendedPubkeysMap = objectMapper.readValue(extendedPubkeys, Map.class);
            return extendedPubkeysMap.entrySet()
                .stream()
                .map(entry -> new ExtendedPubkey(entry.getValue(), entry.getKey()))
                .collect(Collectors.toList());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String serializeExtendedPubkeyEntities(List<ExtendedPubkeyEntity> extendedPubkeys) {
        Map<String, String> extendedPubkeysMap = extendedPubkeys.stream()
            .collect(Collectors.toMap(ExtendedPubkeyEntity::getType, ExtendedPubkeyEntity::getKey));
        return serialize(extendedPubkeysMap);
    }

    private static String serialize(Map<String, String> map) {
        try {
            return objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
