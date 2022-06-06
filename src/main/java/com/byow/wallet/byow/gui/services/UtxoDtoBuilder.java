package com.byow.wallet.byow.gui.services;

import com.byow.wallet.byow.domains.AddressConfig;
import com.byow.wallet.byow.domains.AddressType;
import com.byow.wallet.byow.domains.Utxo;
import com.byow.wallet.byow.domains.UtxoDto;
import com.byow.wallet.byow.observables.CurrentWallet;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UtxoDtoBuilder {
    private final CurrentWallet currentWallet;

    private final List<AddressConfig> addressConfigs;

    public UtxoDtoBuilder(CurrentWallet currentWallet, List<AddressConfig> addressConfigs) {
        this.currentWallet = currentWallet;
        this.addressConfigs = addressConfigs;
    }

    public UtxoDto build(Utxo utxo) {
        Long addressIndex = currentWallet.getAddress(utxo.address()).getIndex();
        AddressType addressType = currentWallet.getAddress(utxo.address()).getAddressType();
        String derivationPath = buildDerivationPath(addressType, addressIndex);
        return new UtxoDto(derivationPath, utxo.amount(), addressType);
    }

    private String buildDerivationPath(AddressType addressType, Long addressIndex) {
        return addressConfigs.stream()
            .filter(addressConfig -> addressConfig.addressType().equals(addressType))
            .findFirst()
            .get()
            .derivationPath()
            .concat("/".concat(addressIndex.toString()));
    }
}
