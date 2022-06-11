package com.byow.wallet.byow.api.services;

import com.byow.wallet.byow.domains.AddressConfig;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AddressConfigFinder {

    private final List<AddressConfig> addressConfigs;

    public AddressConfigFinder(List<AddressConfig> addressConfigs) {
        this.addressConfigs = addressConfigs;
    }

    public Optional<AddressConfig> findByAddress(String address) {
        return addressConfigs.stream()
            .filter(addressConfig -> addressConfig.addressMatcher().test(address))
            .findFirst();
    }

    public Optional<AddressConfig> findByScriptPubkeyType(String scriptType) {
        return addressConfigs.stream()
            .filter(addressConfig -> addressConfig.scriptPubkeyType().equals(scriptType))
            .findFirst();
    }

    public AddressConfig findByAddressType(String addressType) {
        return addressConfigs.stream()
            .filter(addressConfig -> addressConfig.addressType().toString().equals(addressType) ||
                addressConfig.addressType().toString().concat("_CHANGE").equals(addressType)
            )
            .findFirst()
            .orElseThrow();
    }
}
