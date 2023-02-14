package com.byow.wallet.byow.api.services;

import com.byow.wallet.byow.domains.ExtendedPubkey;
import com.byow.wallet.byow.domains.Wallet;
import com.byow.wallet.byow.utils.ExtendedPubkeysSerializer;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class CreateWatchOnlyWalletService {
    private final AddAddressService addAddressService;

    public CreateWatchOnlyWalletService(AddAddressService addAddressService) {
        this.addAddressService = addAddressService;
    }

    public Wallet create(String name, String extendedPubkeysJson, Date createdAt, int numberOfGeneratedAddresses) {
        List<ExtendedPubkey> extendedPubkeys = ExtendedPubkeysSerializer.unserialize(extendedPubkeysJson);
        addAddressService.addAddresses(extendedPubkeys, 0, numberOfGeneratedAddresses);
        return new Wallet(name, extendedPubkeys, createdAt, "");
    }
}
