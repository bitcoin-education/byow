package com.byow.wallet.byow.api

import com.byow.wallet.byow.api.config.AddressConfiguration
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
import static io.github.bitcoineducation.bitcoinjava.AddressConstants.MAINNET_P2WPKH_ADDRESS_PREFIX

class CreateWalletServiceTest extends Specification {

    CreateWalletService createWalletService

    Map<String, AddressConfig> addressConfigs;

    ExtendedPubkeyService extendedPubkeyService

    AddressSequentialGenerator addressSequentialGenerator

    SegwitAddressGenerator segwitAddressGenerator

    AddressConfiguration addressConfigurationMock

    def setup() {
        Security.addProvider(new BouncyCastleProvider())
        addressConfigurationMock = Mock(AddressConfiguration)
        segwitAddressGenerator = new SegwitAddressGenerator(addressConfigurationMock)
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
            addressConfigurationMock.getP2WPKHAddressPrefix() >> MAINNET_P2WPKH_ADDRESS_PREFIX
            Wallet wallet = createWalletService.create(name, password, mnemonicSeed)
        then:
            wallet.name() == name
            wallet.extendedPubkeys().size() == 2
            wallet.extendedPubkeys()[0].addresses.size() == 20
            wallet.extendedPubkeys()[1].addresses.size() == 20
    }
}
