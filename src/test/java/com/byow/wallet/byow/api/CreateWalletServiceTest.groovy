package com.byow.wallet.byow.api

import com.byow.wallet.byow.api.services.AddressSequentialGenerator
import com.byow.wallet.byow.api.services.CreateWalletService
import com.byow.wallet.byow.api.services.ExtendedPubkeyService
import com.byow.wallet.byow.api.services.SegwitAddressGenerator
import com.byow.wallet.byow.domains.AddressConfig
import com.byow.wallet.byow.domains.Wallet
import org.bouncycastle.jce.provider.BouncyCastleProvider
import spock.lang.Specification

import java.security.Security

import static com.byow.wallet.byow.domains.AddressType.SEGWIT
import static com.byow.wallet.byow.domains.AddressType.SEGWIT_CHANGE

class CreateWalletServiceTest extends Specification {

    CreateWalletService createWalletService

    Map<String, AddressConfig> addressConfigs;

    ExtendedPubkeyService extendedPubkeyService

    AddressSequentialGenerator addressSequentialGenerator

    SegwitAddressGenerator segwitAddressGenerator

    def setup() {
        Security.addProvider(new BouncyCastleProvider())
        segwitAddressGenerator = new SegwitAddressGenerator()
        addressSequentialGenerator = new AddressSequentialGenerator(20)
        addressConfigs = [
            "SEGWIT": new AddressConfig(SEGWIT, "84'/0'/0'/0", segwitAddressGenerator),
            "SEGWIT_CHANGE": new AddressConfig(SEGWIT_CHANGE, "84'/0'/0'/1", segwitAddressGenerator)
        ]
        extendedPubkeyService = new ExtendedPubkeyService()
        createWalletService = new CreateWalletService(addressConfigs, extendedPubkeyService, addressSequentialGenerator)
    }

    def "should create wallet"() {
        given:
            String name = "testwallet"
            String password = ""
            String mnemonicSeed = "abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon about"
        when:
            Wallet wallet = createWalletService.create(name, password, mnemonicSeed)
        then:
            wallet.name() == name
            wallet.extendedPubkeys().size() == 2
            wallet.extendedPubkeys()[0].addresses.size() == 20
            wallet.extendedPubkeys()[1].addresses.size() == 20
    }
}
