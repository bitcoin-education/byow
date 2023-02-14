package com.byow.wallet.byow.api.services;

import com.byow.wallet.byow.domains.ExtendedPubkey;
import com.byow.wallet.byow.domains.Wallet;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class CreateWalletService {

    private final AddAddressService addAddressService;

    private final CreateExtendedPubkeysService createExtendedPubkeysService;

    public CreateWalletService(AddAddressService addAddressService, CreateExtendedPubkeysService createExtendedPubkeysService) {
        this.addAddressService = addAddressService;
        this.createExtendedPubkeysService = createExtendedPubkeysService;
    }

    public Wallet create(String name, String password, String mnemonicSeedString, Date createdAt, int numberOfGeneratedAddresses) {
        List<ExtendedPubkey> extendedPubkeys = createExtendedPubkeysService.create(mnemonicSeedString, password);
        addAddressService.addAddresses(extendedPubkeys, 0, numberOfGeneratedAddresses);
        return new Wallet(name, extendedPubkeys, createdAt, mnemonicSeedString);
    }

}
