package com.byow.wallet.byow.api

import com.byow.wallet.byow.api.services.DustCalculator
import com.byow.wallet.byow.api.services.SingleRandomDrawCoinSelector
import com.byow.wallet.byow.api.services.TransactionSizeCalculator
import com.byow.wallet.byow.domains.Utxo
import com.byow.wallet.byow.utils.Satoshi
import spock.lang.Specification

class SingleRandomDrawCoinSelectorTest extends Specification {
    SingleRandomDrawCoinSelector singleRandomDrawCoinSelector

    def setup() {
        singleRandomDrawCoinSelector = new SingleRandomDrawCoinSelector(new TransactionSizeCalculator(), new DustCalculator(3000))
    }

    def "should select #expectedNInputs coins for transaction with #expectedNOutputs outputs"() {
        given:
            List<Utxo> utxos = createUtxos(inputAmounts)
            Utxo additionalUtxo = createUtxo(inputAmounts.last())
            utxos.add(additionalUtxo)
            String addressToSend = "bcrt1q3d5nn9qw9s44cr6g6mh75m0cf4tr7prsfrm5c6"
            String changeAddress = "bcrt1qgykwpz3ql696ct5denuqavk7xcmh6lzwmpcyuk"
            BigDecimal feeRate = 0.0002
        when:
            List<Utxo> selectedUtxos = singleRandomDrawCoinSelector.select(utxos, 100_000_000, feeRate, addressToSend, changeAddress)
        then:
            selectedUtxos.size() == expectedNInputs
        where:
            expectedNInputs | expectedNOutputs | inputAmounts
            1               | 1                | [100_002_090]
            1               | 1                | [100_002_091]
            1               | 1                | [100_002_383]
            1               | 1                | [100_002_384]
            1               | 1                | [100_002_678]
            1               | 1                | [100_002_679]
            1               | 1                | [100_002_972]
            1               | 2                | [100_002_973]
            2               | 1                | [50_001_691, 50_001_691]
            2               | 1                | [50_001_691, 50_001_692]
            2               | 1                | [50_001_691, 50_001_984]
            2               | 1                | [50_001_691, 50_001_985]
            2               | 1                | [50_001_691, 50_002_279]
            2               | 1                | [50_001_691, 50_002_280]
            2               | 1                | [50_001_691, 50_002_573]
            2               | 2                | [50_001_691, 50_002_574]
            3               | 1                | [33_334_891, 33_334_891, 33_334_892]
            3               | 1                | [33_334_891, 33_334_891, 33_334_893]
            3               | 1                | [33_334_891, 33_334_891, 33_335_185]
            3               | 1                | [33_334_891, 33_334_891, 33_335_186]
            3               | 1                | [33_334_891, 33_334_891, 33_335_480]
            3               | 1                | [33_334_891, 33_334_891, 33_335_481]
            3               | 1                | [33_334_891, 33_334_891, 33_335_774]
            3               | 2                | [33_334_891, 33_334_891, 33_335_775]
    }

    List<Utxo> createUtxos(List<BigInteger> amounts) {
        amounts.collect { createUtxo(it) }
    }

    Utxo createUtxo(BigInteger amount) {
        new Utxo(
            "95ca08e71b5b8149b3c70f363e4a29e6a541850be3f143216d85b4b8429183a1",
            0,
            "bcrt1qr8z6uh0pjymq7u70s2xt0vpz5y8kvula632qh6",
            "",
            "001419c5ae5de191360f73cf828cb7b022a10f6673fd",
            Satoshi.toBtc(amount),
            1,
            null,
            null
        )
    }
}
