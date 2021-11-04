package com.byow.wallet.byow.api

import com.byow.wallet.byow.api.services.AddressSequentialGenerator
import com.byow.wallet.byow.domains.Address
import spock.lang.Specification

class AddressSequentialGeneratorTest extends Specification {

    AddressSequentialGenerator addressSequentialGenerator;

    def setup() {
        addressSequentialGenerator = new AddressSequentialGenerator(initialNumberOfGeneratedAddresses, addressGeneratorFactory)
    }

    def "should generate 20 addresses"() {
        when:
            List<Address> addresses = addressSequentialGenerator.generate("zpub6tmUiGj1DxgtbyrfYZUgfrVLuU2gyFPZMkSof4MdWNJaKuas4R1DB9D2arQ52ThKRxfpMbHQeYig45Mt8eNhd5zkFSx81CQyyDYUKRE3y7Y", "SEGWIT")
        then:
            addresses.size() == 20
    }
}
