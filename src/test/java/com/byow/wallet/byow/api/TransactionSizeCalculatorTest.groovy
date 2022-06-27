package com.byow.wallet.byow.api

import com.byow.wallet.byow.api.config.AddressConfiguration
import com.byow.wallet.byow.api.services.AddressConfigFinder
import com.byow.wallet.byow.api.services.TransactionSizeCalculator
import spock.lang.Specification

import static java.util.Collections.nCopies

class TransactionSizeCalculatorTest extends Specification {
    private TransactionSizeCalculator transactionSizeCalculator

    def setup() {
        AddressConfiguration addressConfiguration = new AddressConfiguration()
        def addressConfigs = [
            addressConfiguration.segwitConfig()
        ]
        def addressConfigFinder = new AddressConfigFinder(addressConfigs)
        transactionSizeCalculator = new TransactionSizeCalculator(addressConfigFinder)
    }

    def "should calculate transaction size for P2WPKH transaction outputs and inputs"() {
        when:
            def result = transactionSizeCalculator.calculate(inputs, outputs)
        then:
            result == expectedSize
        where:
            inputs                                                   | outputs                                                  | expectedSize
            nCopies(1, "tb1qkvhn32mj44r6dcwlkl4jtqu3mpe64c8fxq3d40") | nCopies(2, "tb1qkvhn32mj44r6dcwlkl4jtqu3mpe64c8fxq3d40") | 141
            nCopies(2, "tb1qkvhn32mj44r6dcwlkl4jtqu3mpe64c8fxq3d40") | nCopies(2, "tb1qkvhn32mj44r6dcwlkl4jtqu3mpe64c8fxq3d40") | 209
            nCopies(3, "tb1qkvhn32mj44r6dcwlkl4jtqu3mpe64c8fxq3d40") | nCopies(2, "tb1qkvhn32mj44r6dcwlkl4jtqu3mpe64c8fxq3d40") | 277
            nCopies(1, "tb1qkvhn32mj44r6dcwlkl4jtqu3mpe64c8fxq3d40") | nCopies(1, "tb1qkvhn32mj44r6dcwlkl4jtqu3mpe64c8fxq3d40") | 110
            nCopies(2, "tb1qkvhn32mj44r6dcwlkl4jtqu3mpe64c8fxq3d40") | nCopies(1, "tb1qkvhn32mj44r6dcwlkl4jtqu3mpe64c8fxq3d40") | 178
            nCopies(3, "tb1qkvhn32mj44r6dcwlkl4jtqu3mpe64c8fxq3d40") | nCopies(1, "tb1qkvhn32mj44r6dcwlkl4jtqu3mpe64c8fxq3d40") | 246
    }
}
