package com.byow.wallet.byow.api

import com.byow.wallet.byow.api.config.AddressConfiguration
import com.byow.wallet.byow.api.config.ScriptConfiguration
import com.byow.wallet.byow.api.services.AddressConfigFinder
import com.byow.wallet.byow.api.services.ScriptConfigFinder
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
        ScriptConfiguration scriptConfiguration = new ScriptConfiguration()
        def scriptConfigFinder = new ScriptConfigFinder(List.of(
            scriptConfiguration.P2WPKHConfig(),
            scriptConfiguration.P2SHConfig(),
            scriptConfiguration.P2PKHConfig()
        ))
        transactionSizeCalculator = new TransactionSizeCalculator(addressConfigFinder, scriptConfigFinder)
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

    def "should calculate transaction size for P2WPKH transaction inputs and P2PKH transaction outputs"() {
        when:
            def result = transactionSizeCalculator.calculate(inputs, outputs)
        then:
            result == expectedSize
        where:
            inputs                                                   | outputs                                          | expectedSize
            nCopies(1, "tb1qkvhn32mj44r6dcwlkl4jtqu3mpe64c8fxq3d40") | nCopies(2, "mu27DxrNovbD24uZJJXHnPxEDJkAZBCYN6") | 147
            nCopies(2, "tb1qkvhn32mj44r6dcwlkl4jtqu3mpe64c8fxq3d40") | nCopies(2, "mu27DxrNovbD24uZJJXHnPxEDJkAZBCYN6") | 215
            nCopies(3, "tb1qkvhn32mj44r6dcwlkl4jtqu3mpe64c8fxq3d40") | nCopies(2, "mu27DxrNovbD24uZJJXHnPxEDJkAZBCYN6") | 283
            nCopies(1, "tb1qkvhn32mj44r6dcwlkl4jtqu3mpe64c8fxq3d40") | nCopies(1, "mu27DxrNovbD24uZJJXHnPxEDJkAZBCYN6") | 113
            nCopies(2, "tb1qkvhn32mj44r6dcwlkl4jtqu3mpe64c8fxq3d40") | nCopies(1, "mu27DxrNovbD24uZJJXHnPxEDJkAZBCYN6") | 181
            nCopies(3, "tb1qkvhn32mj44r6dcwlkl4jtqu3mpe64c8fxq3d40") | nCopies(1, "mu27DxrNovbD24uZJJXHnPxEDJkAZBCYN6") | 249
    }
}
