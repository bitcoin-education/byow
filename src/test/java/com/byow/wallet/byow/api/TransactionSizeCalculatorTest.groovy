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
            addressConfiguration.segwitConfig(),
            addressConfiguration.nestedSegwitConfig()
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

    def "should calculate transaction size for P2SH-P2WPKH transaction outputs and inputs"() {
        when:
            def result = transactionSizeCalculator.calculate(inputs, outputs)
        then:
            result == expectedSize
        where:
            inputs                                            | outputs                                           | expectedSize
            nCopies(1, "2N2JnaoXzjcMYPWbofDQXT2wxy2ecUHKMgp") | nCopies(2, "2N2JnaoXzjcMYPWbofDQXT2wxy2ecUHKMgp") | 166
            nCopies(2, "2N2JnaoXzjcMYPWbofDQXT2wxy2ecUHKMgp") | nCopies(2, "2N2JnaoXzjcMYPWbofDQXT2wxy2ecUHKMgp") | 257
            nCopies(3, "2N2JnaoXzjcMYPWbofDQXT2wxy2ecUHKMgp") | nCopies(2, "2N2JnaoXzjcMYPWbofDQXT2wxy2ecUHKMgp") | 348
            nCopies(1, "2N2JnaoXzjcMYPWbofDQXT2wxy2ecUHKMgp") | nCopies(1, "2N2JnaoXzjcMYPWbofDQXT2wxy2ecUHKMgp") | 134
            nCopies(2, "2N2JnaoXzjcMYPWbofDQXT2wxy2ecUHKMgp") | nCopies(1, "2N2JnaoXzjcMYPWbofDQXT2wxy2ecUHKMgp") | 225
            nCopies(3, "2N2JnaoXzjcMYPWbofDQXT2wxy2ecUHKMgp") | nCopies(1, "2N2JnaoXzjcMYPWbofDQXT2wxy2ecUHKMgp") | 316
    }
}
