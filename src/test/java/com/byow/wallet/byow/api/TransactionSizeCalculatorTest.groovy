package com.byow.wallet.byow.api

import com.byow.wallet.byow.api.services.TransactionSizeCalculator
import spock.lang.Specification

class TransactionSizeCalculatorTest extends Specification {
    private TransactionSizeCalculator transactionSizeCalculator

    def setup() {
        transactionSizeCalculator = new TransactionSizeCalculator();
    }

    def "should calculate transaction size for P2WPKH transaction outputs and inputs"() {
        when:
            def result = transactionSizeCalculator.calculate(inputs, outputs)
        then:
            result == expectedSize
        where:
            inputs              | outputs       | expectedSize
            ["a"]               | ["a", "b"]    | 141
            ["a", "b"]          | ["a", "b"]    | 209
            ["a", "b", "c"]     | ["a", "b"]    | 277
            ["a"]               | ["a"]         | 110
            ["a", "b"]          | ["a"]         | 178
            ["a", "b", "c"]     | ["a"]         | 246
    }
}
