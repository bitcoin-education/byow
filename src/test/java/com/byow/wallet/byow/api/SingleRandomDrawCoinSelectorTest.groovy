package com.byow.wallet.byow.api

import com.byow.wallet.byow.Utils
import com.byow.wallet.byow.api.services.DustCalculator
import com.byow.wallet.byow.api.services.SingleRandomDrawCoinSelector
import com.byow.wallet.byow.api.services.TransactionSizeCalculator
import com.byow.wallet.byow.domains.Utxo
import spock.lang.Specification

class SingleRandomDrawCoinSelectorTest extends Specification {
    SingleRandomDrawCoinSelector singleRandomDrawCoinSelector

    def setup() {
        singleRandomDrawCoinSelector = new SingleRandomDrawCoinSelector(new TransactionSizeCalculator(), new DustCalculator(3000))
    }

    def "should select #expectedNInputs coins for transaction with #expectedNOutputs outputs"() {
        given:
            List<Utxo> utxos = Utils.createUtxos(inputAmounts)
            Utxo additionalUtxo = Utils.createUtxo(inputAmounts.last())
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

}
