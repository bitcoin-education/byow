package com.byow.wallet.byow.api

import com.byow.wallet.byow.api.services.AddressGeneratorFactory
import com.byow.wallet.byow.api.services.AddressPrefixFactory
import com.byow.wallet.byow.api.services.AddressSequentialGenerator
import com.byow.wallet.byow.api.services.SegwitAddressGenerator
import com.byow.wallet.byow.domains.Address
import org.bouncycastle.jce.provider.BouncyCastleProvider
import spock.lang.Specification

import java.security.Security

import static com.byow.wallet.byow.api.services.AddressPrefixFactory.MAINNET

class AddressSequentialGeneratorTest extends Specification {

    AddressSequentialGenerator addressSequentialGenerator
    AddressGeneratorFactory addressGeneratorFactory
    AddressPrefixFactory addressPrefixFactory

    def setup() {
        Security.addProvider(new BouncyCastleProvider())
        addressPrefixFactory = new AddressPrefixFactory(MAINNET)
        addressGeneratorFactory = new AddressGeneratorFactory(new SegwitAddressGenerator(addressPrefixFactory))
        addressSequentialGenerator = new AddressSequentialGenerator(20, addressGeneratorFactory)
    }

    def "should generate 20 addresses"() {
        when:
            List<Address> addresses = addressSequentialGenerator.generate("zpub6tmUiGj1DxgtbyrfYZUgfrVLuU2gyFPZMkSof4MdWNJaKuas4R1DB9D2arQ52ThKRxfpMbHQeYig45Mt8eNhd5zkFSx81CQyyDYUKRE3y7Y", "SEGWIT")
        then:
            addresses.size() == 20
    }
}
