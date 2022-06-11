package com.byow.wallet.byow.gui.services;

import com.byow.wallet.byow.api.services.AddressConfigFinder;
import com.byow.wallet.byow.domains.AddressType;
import com.byow.wallet.byow.domains.Utxo;
import com.byow.wallet.byow.domains.UtxoDto;
import com.byow.wallet.byow.observables.CurrentWallet;
import org.springframework.stereotype.Service;

@Service
public class UtxoDtoBuilder {
    private final CurrentWallet currentWallet;

    private final AddressConfigFinder addressConfigFinder;

    public UtxoDtoBuilder(CurrentWallet currentWallet, AddressConfigFinder addressConfigFinder) {
        this.currentWallet = currentWallet;
        this.addressConfigFinder = addressConfigFinder;
    }

    public UtxoDto build(Utxo utxo) {
        Long addressIndex = currentWallet.getAddress(utxo.address()).getIndex();
        AddressType addressType = currentWallet.getAddress(utxo.address()).getAddressType();
        String derivationPath = buildDerivationPath(addressType, addressIndex);
        return new UtxoDto(derivationPath, utxo.amount(), addressType);
    }

    private String buildDerivationPath(AddressType addressType, Long addressIndex) {
        return addressConfigFinder.findByAddressType(addressType.toString())
            .derivationPaths()
            .get(addressType)
            .concat("/".concat(addressIndex.toString()));
    }
}
