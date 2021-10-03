package com.byow.wallet.byow.api.services;

import com.byow.wallet.byow.domains.Address;
import com.byow.wallet.byow.domains.Utxo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class UtxoService {

    @Autowired
    RestTemplate restTemplate;

    public List<Utxo> getUtxos(List<Address> addressList) {
        Utxo[] utxosArray = restTemplate.postForEntity("/", Map.of(
                "method", "listunspent",
                "params", List.of(
                        0,
                        Integer.MAX_VALUE,
                        addressList
                    )
            ), Utxo[].class
        ).getBody();

        if (utxosArray != null) {
            return Arrays.stream(utxosArray).toList();
        }

        return List.of();
    }
}
