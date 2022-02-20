package com.byow.wallet.byow.api

import com.byow.wallet.byow.api.services.AddAddressService
import com.byow.wallet.byow.api.services.AddressGeneratorFactory
import com.byow.wallet.byow.api.services.AddressPrefixFactory
import com.byow.wallet.byow.api.services.AddressSequentialGenerator
import com.byow.wallet.byow.api.services.CreateWalletService
import com.byow.wallet.byow.api.services.ExtendedPubkeyService
import com.byow.wallet.byow.api.services.SegwitAddressGenerator
import com.byow.wallet.byow.domains.AddressConfig
import com.byow.wallet.byow.domains.Wallet
import org.bouncycastle.jce.provider.BouncyCastleProvider
import spock.lang.Specification

import java.security.Security

import static com.byow.wallet.byow.api.services.AddressPrefixFactory.MAINNET
import static com.byow.wallet.byow.domains.AddressType.SEGWIT
import static com.byow.wallet.byow.domains.AddressType.SEGWIT_CHANGE

class CreateWalletServiceTest extends Specification {

    CreateWalletService createWalletService

    List<AddressConfig> addressConfigs

    ExtendedPubkeyService extendedPubkeyService

    AddressGeneratorFactory addressGeneratorFactory

    SegwitAddressGenerator segwitAddressGenerator

    AddressPrefixFactory addressPrefixFactory

    def setup() {
        Security.addProvider(new BouncyCastleProvider())
        addressConfigs = [
            new AddressConfig(SEGWIT, "84'/0'/0'/0"),
            new AddressConfig(SEGWIT_CHANGE, "84'/0'/0'/1")
        ]
        extendedPubkeyService = new ExtendedPubkeyService()
        addressPrefixFactory = new AddressPrefixFactory(MAINNET)
        segwitAddressGenerator = new SegwitAddressGenerator(addressPrefixFactory)
        addressGeneratorFactory = new AddressGeneratorFactory(segwitAddressGenerator)
        AddressSequentialGenerator addressSequentialGenerator = new AddressSequentialGenerator(20, addressGeneratorFactory)
        AddAddressService addAddressService = new AddAddressService(addressSequentialGenerator)
        createWalletService = new CreateWalletService(addressConfigs, extendedPubkeyService, addAddressService)
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
