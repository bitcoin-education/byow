package com.byow.wallet.byow.api

import com.byow.wallet.byow.api.services.MnemonicSeedService
import spock.lang.Specification

class MnemonicSeedServiceTest extends Specification {

    MnemonicSeedService mnemonicSeedService = new MnemonicSeedService()

    def "should return random mnemonic seed"() {
        expect:
            mnemonicSeedService.create()
    }
}
