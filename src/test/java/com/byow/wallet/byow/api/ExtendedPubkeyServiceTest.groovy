package com.byow.wallet.byow.api

import com.byow.wallet.byow.api.services.ExtendedPubkeyService
import com.byow.wallet.byow.domains.ExtendedPubkey
import io.github.bitcoineducation.bitcoinjava.ExtendedKeyPrefixes
import io.github.bitcoineducation.bitcoinjava.ExtendedPrivateKey
import io.github.bitcoineducation.bitcoinjava.MnemonicSeed
import org.bouncycastle.jce.provider.BouncyCastleProvider
import spock.lang.Specification

import java.security.Security

import static com.byow.wallet.byow.domains.AddressType.SEGWIT
import static com.byow.wallet.byow.domains.AddressType.SEGWIT_CHANGE

class ExtendedPubkeyServiceTest extends Specification {
    ExtendedPubkeyService extendedPubkeyService = new ExtendedPubkeyService()

    def setup() {
        Security.addProvider(new BouncyCastleProvider())
    }

    def "should create extended key"() {
        given:
            MnemonicSeed mnemonicSeed = new MnemonicSeed(mnemonicSeedString)
            ExtendedPrivateKey masterKey = mnemonicSeed.toMasterKey(password, ExtendedKeyPrefixes.MAINNET_PREFIX.privatePrefix)
        when:
            ExtendedPubkey extendedPubkey = extendedPubkeyService.create(masterKey, derivationPath, addressType, ExtendedKeyPrefixes.MAINNET_SEGWIT_PREFIX)
        then:
            extendedPubkey.key == expectedExtendedPubkey
        where:
            mnemonicSeedString                                                                                  | password      | derivationPath | addressType   | expectedExtendedPubkey
            "abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon about"     | ""            | "84'/0'/0'/0"  | SEGWIT        | "zpub6u4KbU8TSgNuZSxzv7HaGq5Tk361gMHdZxnM4UYuwzg5CMLcNytzhobitV4Zq6vWtWHpG9QijsigkxAzXvQWyLRfLq1L7VxPP1tky1hPfD4"
            "select truly engine hotel desk monster suit patient awake ripple cushion plug distance wire spare" | ""            | "84'/0'/0'/0"  | SEGWIT        | "zpub6saHjxJhDCZ3DENrhPd2BEJWwBfBjMc2uPBzPPUETVPVbMsCqsSP1iJkeZV7RbVWwKuHNVXGLn4MSQ889oMXMa6z63fFyvmS6PnpMf7bGoh"
            "light seat build slim phone nature much hockey success loop green essay rude ten reveal"           | "passphrase"  | "84'/0'/0'/0"  | SEGWIT        | "zpub6tc8SZZNpY9eUeRXzYGA7wbLqUCoGC7Xh7TfGR5GVNmPa3YV3AQcUgiJ11mcGXDUfQ3p59mwDk9dPXk9ph5r9wLCksGXpgBpWPAECpPszhN"
            "reveal pelican alpha inch crawl salute barrel reason ticket escape addict chest differ blue blue"  | "passphrase2" | "84'/0'/0'/1"  | SEGWIT_CHANGE | "zpub6tauv9h58FY4goyRkqk1TjNW4uQBA7m7wSYF5QZCGB3Xu9KxHbJUHBa24SLevFnQ8sT34LcppmgtzRiPv55W3Vxf5TKTMoneQthE3TzPALF"
            "birth travel bar owner giant mail meat river luxury latin crush mule angry submit own"             | "passphrase3" | "84'/0'/0'/1"  | SEGWIT_CHANGE | "zpub6t5ZhE77tcLpX723WQeKFREuUPR2PGWM5V8AUdx3ss8TNZuqSSDhCmE19jMMLZjR6GfVgpnrnTPSZ2s3HJRCyG8thYHivg1ASzT6mfENMHE"
    }
}

