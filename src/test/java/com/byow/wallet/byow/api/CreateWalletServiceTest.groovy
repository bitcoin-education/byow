package com.byow.wallet.byow.api

import com.byow.wallet.byow.api.services.CreateWalletService
import com.byow.wallet.byow.domains.Wallet
import spock.lang.Specification

class CreateWalletServiceTest extends Specification {

    CreateWalletService createWalletService

    def setup() {
        createWalletService = new CreateWalletService()
    }

    def "should create wallet"() {
        given:
            String name = "testwallet"
            String password = ""
            String mnemonicSeed = ""
        when:
            Wallet wallet = createWalletService.create(name, password, mnemonicSeed)
        then:
            wallet.name() == name
    }
}
