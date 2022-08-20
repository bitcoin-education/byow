package com.byow.wallet.byow.api

import com.byow.wallet.byow.api.services.NestedSegwitAddressGenerator
import io.github.bitcoineducation.bitcoinjava.AddressConstants
import io.github.bitcoineducation.bitcoinjava.ExtendedKey
import io.github.bitcoineducation.bitcoinjava.ExtendedPubkey
import org.bouncycastle.jce.provider.BouncyCastleProvider
import spock.lang.Specification

import java.security.Security

class NestedSegwitAddressGeneratorTest extends Specification {
    NestedSegwitAddressGenerator nestedSegwitAddressGenerator

    def setup() {
        Security.addProvider(new BouncyCastleProvider())
        nestedSegwitAddressGenerator = new NestedSegwitAddressGenerator()
    }

    def "should generate segwit address"() {
        given:
            ExtendedPubkey extendedPubkey = ExtendedPubkey.unserialize(extendedPubkeyString)
            ExtendedKey extendedChildKey = extendedPubkey.ckd(index)
        when:
            String address = nestedSegwitAddressGenerator.generate(extendedChildKey, AddressConstants.MAINNET_P2SH_ADDRESS_PREFIX)
        then:
            address == expectedAddress
        where:
            extendedPubkeyString                                                                                                | index | expectedAddress
            "ypub6ZQfVRjWzEcKn1dVKxJEsZw5pCqib32ACKvANLeD8VuY12Ghku7sH49DaZYZK4CDU37bEcahoSMRwAH8bhUvuE7wzLwoLxfpYFWPQzMdVMg"   | "0"   | "333AjFFpCW1msMgZ9oUJCHXpqDTnEhNqhq"
            "ypub6ZQfVRjWzEcKn1dVKxJEsZw5pCqib32ACKvANLeD8VuY12Ghku7sH49DaZYZK4CDU37bEcahoSMRwAH8bhUvuE7wzLwoLxfpYFWPQzMdVMg"   | "1"   | "327xTHdwwyVnWd6Rk4rAvjUW5p4rpnPUxC"
            "ypub6ZQfVRjWzEcKn1dVKxJEsZw5pCqib32ACKvANLeD8VuY12Ghku7sH49DaZYZK4CDU37bEcahoSMRwAH8bhUvuE7wzLwoLxfpYFWPQzMdVMg"   | "2"   | "3GJW2Ev8BqSJGYefZFSGZbo53x7DEpZw7K"
            "ypub6ZQfVRjWzEcKn1dVKxJEsZw5pCqib32ACKvANLeD8VuY12Ghku7sH49DaZYZK4CDU37bEcahoSMRwAH8bhUvuE7wzLwoLxfpYFWPQzMdVMg"   | "3"   | "3NVaqfGBwyci8RjA6AUpu5m1NQB8CoLdSq"
    }

}
