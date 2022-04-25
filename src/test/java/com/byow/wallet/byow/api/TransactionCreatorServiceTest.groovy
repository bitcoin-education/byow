package com.byow.wallet.byow.api

import com.byow.wallet.byow.Utils
import com.byow.wallet.byow.api.services.DustCalculator
import com.byow.wallet.byow.api.services.TransactionCreatorService
import com.byow.wallet.byow.domains.Utxo
import com.byow.wallet.byow.utils.Satoshi
import io.github.bitcoineducation.bitcoinjava.Transaction
import spock.lang.Specification

class TransactionCreatorServiceTest extends Specification {
    TransactionCreatorService transactionCreatorService

    def setup() {
        transactionCreatorService = new TransactionCreatorService(new DustCalculator(3000))
    }

    def "should create transaction with #nInputs inputs and #expectedNOutputs outputs"() {
        given:
            List<Utxo> utxos = Utils.createUtxos(inputAmounts)
            String addressToSend = "bcrt1q3d5nn9qw9s44cr6g6mh75m0cf4tr7prsfrm5c6"
            String changeAddress = "bcrt1qgykwpz3ql696ct5denuqavk7xcmh6lzwmpcyuk"
            BigDecimal feeRate = 0.0002
            BigInteger expectedTotalFee = expectedSize * Satoshi.btcPerKbToSatoshiPerByte(feeRate)
            BigInteger totalInputAmount = utxos.sum{Satoshi.toSatoshis(it.amount())}
            BigInteger amountToSend = 100_000_000
            BigInteger expectedChangeAmount = totalInputAmount - amountToSend - expectedTotalFee
        when:
            Transaction transaction = transactionCreatorService.create(utxos, addressToSend, amountToSend, changeAddress, feeRate)
        then:
            transaction.getVSize() == expectedSize
            transaction.outputs.size() == expectedNOutputs
            transaction.outputs[0].amount == amountToSend
            if (expectedNOutputs > 1) {
                assert transaction.outputs[1].amount == expectedChangeAmount
            }
        where:
            nInputs         | expectedNOutputs | inputAmounts                         | expectedSize | changeIsDust
            1               | 1                | [100_002_090]                        | 110          | true
            1               | 1                | [100_002_091]                        | 110          | true
            1               | 1                | [100_002_383]                        | 110          | true
            1               | 1                | [100_002_384]                        | 110          | true
            1               | 1                | [100_002_678]                        | 110          | true
            1               | 1                | [100_002_679]                        | 110          | true
            1               | 1                | [100_002_972]                        | 110          | true
            1               | 2                | [100_002_973]                        | 141          | false
            2               | 1                | [50_001_691, 50_001_691]             | 178          | true
            2               | 1                | [50_001_691, 50_001_692]             | 178          | true
            2               | 1                | [50_001_691, 50_001_984]             | 178          | true
            2               | 1                | [50_001_691, 50_001_985]             | 178          | true
            2               | 1                | [50_001_691, 50_002_279]             | 178          | true
            2               | 1                | [50_001_691, 50_002_280]             | 178          | true
            2               | 1                | [50_001_691, 50_002_573]             | 178          | true
            2               | 2                | [50_001_691, 50_002_574]             | 209          | false
            3               | 1                | [33_334_891, 33_334_891, 33_334_892] | 246          | true
            3               | 1                | [33_334_891, 33_334_891, 33_334_893] | 246          | true
            3               | 1                | [33_334_891, 33_334_891, 33_335_185] | 246          | true
            3               | 1                | [33_334_891, 33_334_891, 33_335_186] | 246          | true
            3               | 1                | [33_334_891, 33_334_891, 33_335_480] | 246          | true
            3               | 1                | [33_334_891, 33_334_891, 33_335_481] | 246          | true
            3               | 1                | [33_334_891, 33_334_891, 33_335_774] | 246          | true
            3               | 2                | [33_334_891, 33_334_891, 33_335_775] | 277          | false
    }
}
