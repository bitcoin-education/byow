package com.byow.wallet.byow.api

import com.byow.wallet.byow.api.services.AddressPrefixFactory
import com.byow.wallet.byow.api.services.SegwitAddressGenerator
import io.github.bitcoineducation.bitcoinjava.ExtendedKey
import io.github.bitcoineducation.bitcoinjava.ExtendedPubkey
import org.bouncycastle.jce.provider.BouncyCastleProvider
import spock.lang.Specification

import java.security.Security

import static com.byow.wallet.byow.api.services.AddressPrefixFactory.MAINNET

class SegwitAddressGeneratorTest extends Specification {
    SegwitAddressGenerator segwitAddressGenerator
    AddressPrefixFactory addressPrefixFactory

    def setup() {
        Security.addProvider(new BouncyCastleProvider())
        addressPrefixFactory = new AddressPrefixFactory(MAINNET)
        segwitAddressGenerator = new SegwitAddressGenerator(addressPrefixFactory)
    }

    def "should generate segwit address"() {
        given:
            ExtendedPubkey extendedPubkey = ExtendedPubkey.unserialize(extendedPubkeyString)
            ExtendedKey extendedChildKey = extendedPubkey.ckd(index)
        when:
            String address = segwitAddressGenerator.generate(extendedChildKey)
        then:
            address == expectedAddress
        where:
            extendedPubkeyString                                                                                                | index | expectedAddress
            "zpub6tyuNX76w7PJuX5WXxP9AFAnEtKj2jWrcBwAzcgTihLwrrsrrTEgoKZmokyYyasdo36KEHdm5SfpWzyb7DfW39SZFQucTc6SBrqdLK6npQ7"   | "0"   | "bc1qe9gz0sv5kzypssntsfglgd42uy8ruhqyeq2jrl"
            "zpub6tyuNX76w7PJuX5WXxP9AFAnEtKj2jWrcBwAzcgTihLwrrsrrTEgoKZmokyYyasdo36KEHdm5SfpWzyb7DfW39SZFQucTc6SBrqdLK6npQ7"   | "1"   | "bc1qs7svngemn4edqcj7m4zeddguuwh9xpwx4q6wpj"
            "zpub6tyuNX76w7PJuX5WXxP9AFAnEtKj2jWrcBwAzcgTihLwrrsrrTEgoKZmokyYyasdo36KEHdm5SfpWzyb7DfW39SZFQucTc6SBrqdLK6npQ7"   | "2"   | "bc1qthl293xrzfwl6ckyas6a6mv3ulltcd5lg2s5x4"
            "zpub6tyuNX76w7PJuX5WXxP9AFAnEtKj2jWrcBwAzcgTihLwrrsrrTEgoKZmokyYyasdo36KEHdm5SfpWzyb7DfW39SZFQucTc6SBrqdLK6npQ7"   | "3"   | "bc1qx5a9cnxn783lzhke4e543rda38y6lyy250er87"
    }

}
