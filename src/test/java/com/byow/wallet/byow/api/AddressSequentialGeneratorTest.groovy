package com.byow.wallet.byow.api

import com.byow.wallet.byow.api.config.AddressConfiguration
import com.byow.wallet.byow.api.services.AddressConfigFinder
import com.byow.wallet.byow.api.services.AddressGeneratorFactory
import com.byow.wallet.byow.api.services.AddressPrefixFactory
import com.byow.wallet.byow.api.services.AddressSequentialGenerator
import com.byow.wallet.byow.domains.Address
import org.bouncycastle.jce.provider.BouncyCastleProvider
import spock.lang.Specification

import java.security.Security

import static org.mockito.ArgumentMatchers.any
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

class AddressSequentialGeneratorTest extends Specification {

    AddressSequentialGenerator addressSequentialGenerator
    AddressGeneratorFactory addressGeneratorFactory
    AddressPrefixFactory addressPrefixFactory
    AddressConfigFinder addressConfigFinder

    def setup() {
        Security.addProvider(new BouncyCastleProvider())
        addressConfigFinder = mock(AddressConfigFinder)
        def addressConfiguration = new AddressConfiguration()
        when(addressConfigFinder.findByAddressType(any())).thenReturn(addressConfiguration.segwitConfig())

        addressPrefixFactory = new AddressPrefixFactory("mainnet", addressConfigFinder)
        addressGeneratorFactory = new AddressGeneratorFactory(addressConfigFinder)
        addressSequentialGenerator = new AddressSequentialGenerator(addressGeneratorFactory, addressPrefixFactory)
    }

    def "should generate 20 addresses"() {
        when:
            List<Address> addresses = addressSequentialGenerator.generate("zpub6tmUiGj1DxgtbyrfYZUgfrVLuU2gyFPZMkSof4MdWNJaKuas4R1DB9D2arQ52ThKRxfpMbHQeYig45Mt8eNhd5zkFSx81CQyyDYUKRE3y7Y", "SEGWIT", 0, 20)
        then:
            addresses.size() == 20
    }
}
