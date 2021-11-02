package com.byow.wallet.byow.api

import com.byow.wallet.byow.api.config.AddressConfiguration
import com.byow.wallet.byow.api.services.AddressGeneratorFactory
import com.byow.wallet.byow.api.services.AddressSequentialGenerator
import com.byow.wallet.byow.api.services.SegwitAddressGenerator
import com.byow.wallet.byow.domains.Address
import org.bouncycastle.jce.provider.BouncyCastleProvider
import spock.lang.Specification

import java.security.Security

import static io.github.bitcoineducation.bitcoinjava.AddressConstants.MAINNET_P2WPKH_ADDRESS_PREFIX

class AddressSequentialGeneratorTest extends Specification {
    AddressSequentialGenerator addressSequentialGenerator
    AddressGeneratorFactory addressGeneratorFactory
    AddressConfiguration addressConfigurationMock

    def setup() {
        addressConfigurationMock = Mock(AddressConfiguration)
        addressGeneratorFactory = new AddressGeneratorFactory(new SegwitAddressGenerator(addressConfigurationMock))
        addressSequentialGenerator = new AddressSequentialGenerator(20, addressGeneratorFactory)
        Security.addProvider(new BouncyCastleProvider())
    }

    def "should generate 20 addresses"() {
        when:
            List<Address> addresses = addressSequentialGenerator.generate("zpub6tmUiGj1DxgtbyrfYZUgfrVLuU2gyFPZMkSof4MdWNJaKuas4R1DB9D2arQ52ThKRxfpMbHQeYig45Mt8eNhd5zkFSx81CQyyDYUKRE3y7Y", "SEGWIT")
        then:
            20 * addressConfigurationMock.getP2WPKHAddressPrefix() >> MAINNET_P2WPKH_ADDRESS_PREFIX
            addresses.size() == 20
    }
}
