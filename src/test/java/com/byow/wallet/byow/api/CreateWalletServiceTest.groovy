package com.byow.wallet.byow.api

import com.byow.wallet.byow.api.config.AddressConfiguration
import com.byow.wallet.byow.api.services.AddAddressService
import com.byow.wallet.byow.api.services.AddressConfigFinder
import com.byow.wallet.byow.api.services.AddressGeneratorFactory
import com.byow.wallet.byow.api.services.AddressPrefixFactory
import com.byow.wallet.byow.api.services.AddressSequentialGenerator
import com.byow.wallet.byow.api.services.CreateExtendedPubkeysService
import com.byow.wallet.byow.api.services.CreateWalletService
import com.byow.wallet.byow.api.services.ExtendedKeyPrefixFactory
import com.byow.wallet.byow.api.services.ExtendedPubkeyService
import com.byow.wallet.byow.domains.Environment
import com.byow.wallet.byow.domains.Wallet
import org.bouncycastle.jce.provider.BouncyCastleProvider
import spock.lang.Specification

import java.security.Security

class CreateWalletServiceTest extends Specification {

    CreateWalletService createWalletService

    def setup() {
        Security.addProvider(new BouncyCastleProvider())
        AddressConfiguration addressConfiguration = new AddressConfiguration(bitcoinEnvironment: Environment.REGTEST)
        def addressConfigs = [
            addressConfiguration.segwitConfig()
        ]
        def addressConfigFinder = new AddressConfigFinder(addressConfigs)
        def addressPrefixFactory = new AddressPrefixFactory("MAINNET", addressConfigFinder)
        def extendedPubkeyService = new ExtendedPubkeyService()
        def addressGeneratorFactory = new AddressGeneratorFactory(addressConfigFinder)
        def addressSequentialGenerator = new AddressSequentialGenerator(addressGeneratorFactory, addressPrefixFactory)
        def extendedKeyPrefixFactory = new ExtendedKeyPrefixFactory(Environment.REGTEST.toString())
        def createExtendedPubkeysService = new CreateExtendedPubkeysService(addressConfigs, extendedPubkeyService, extendedKeyPrefixFactory)

        AddAddressService addAddressService = new AddAddressService(addressSequentialGenerator)
        createWalletService = new CreateWalletService(addAddressService, createExtendedPubkeysService)
    }

    def "should create wallet"() {
        given:
            String name = "testwallet"
            String password = ""
            String mnemonicSeed = "abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon about"
        when:
            Wallet wallet = createWalletService.create(name, password, mnemonicSeed, new Date(), 20)
        then:
            wallet.name() == name
            wallet.extendedPubkeys().size() == 2
            wallet.extendedPubkeys()[0].addresses.size() == 20
            wallet.extendedPubkeys()[1].addresses.size() == 20
    }
}
