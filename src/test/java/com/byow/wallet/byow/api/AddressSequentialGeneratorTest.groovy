package com.byow.wallet.byow.api

import com.byow.wallet.byow.api.services.AddressSequentialGenerator
import com.byow.wallet.byow.api.services.SegwitAddressGenerator
import com.byow.wallet.byow.domains.Address
import org.bouncycastle.jce.provider.BouncyCastleProvider
import spock.lang.Specification

import java.security.Security

class AddressSequentialGeneratorTest extends Specification {
    AddressSequentialGenerator addressSequentialGenerator

    def setup() {
        addressSequentialGenerator = new AddressSequentialGenerator(20)
        Security.addProvider(new BouncyCastleProvider())
    }

    def "should generate 20 addresses"() {
        when:
            List<Address> addresses = addressSequentialGenerator.generate("zpub6tmUiGj1DxgtbyrfYZUgfrVLuU2gyFPZMkSof4MdWNJaKuas4R1DB9D2arQ52ThKRxfpMbHQeYig45Mt8eNhd5zkFSx81CQyyDYUKRE3y7Y", new SegwitAddressGenerator())
        then:
            addresses.size() == 20
    }
}
