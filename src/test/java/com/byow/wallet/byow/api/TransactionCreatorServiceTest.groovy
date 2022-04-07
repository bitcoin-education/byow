package com.byow.wallet.byow.api

import com.byow.wallet.byow.api.services.DustCalculator
import com.byow.wallet.byow.api.services.TransactionCreatorService
import com.byow.wallet.byow.domains.Utxo
import com.byow.wallet.byow.utils.Satoshi
import io.github.bitcoineducation.bitcoinjava.Transaction
import spock.lang.Specification

import java.util.stream.IntStream

class TransactionCreatorServiceTest extends Specification {
    TransactionCreatorService transactionCreatorService

    def setup() {
        transactionCreatorService = new TransactionCreatorService(new DustCalculator(3000))
    }

    def "should create transaction with #nInputs inputs and #expectedNOutputs outputs"() {
        given:
            List<Utxo> utxos = createUtxo(nInputs, inputAmount)
            String addressToSend = "bcrt1q3d5nn9qw9s44cr6g6mh75m0cf4tr7prsfrm5c6"
            String changeAddress = "bcrt1qgykwpz3ql696ct5denuqavk7xcmh6lzwmpcyuk"
            BigDecimal feeRate = 0.0002
            BigInteger expectedTotalFee = expectedSize * Satoshi.btcPerKbToSatoshiPerByte(feeRate)
            BigInteger totalInputAmount = utxos.sum{Satoshi.toSatoshis(it.amount())}
            BigInteger expectedChangeAmount = totalInputAmount - amountToSend - expectedTotalFee
        when:
            Transaction transaction = transactionCreatorService.create(utxos, addressToSend, amountToSend, changeAddress, feeRate)
        then:
            transaction.getVSize() == expectedSize
            transaction.outputs.size() == expectedNOutputs
            transaction.outputs[0].amount == amountToSend
            if (expectedNOutputs > 1) {
                assert transaction.outputs[1].amount == expectedChangeAmount
            } else if(!changeIsDust) {
                assert expectedChangeAmount == 0
            }
        where:
            nInputs         | expectedNOutputs | inputAmount | amountToSend | expectedSize | changeIsDust
            1               | 2                | 1.00000000  | 50_000_000   | 141          | false
            1               | 1                | 1.00002090  | 100_000_000  | 110          | false // total fee = 2090
            2               | 2                | 1.00000000  | 150_000_000  | 209          | false
            2               | 1                | 1.00001691  | 200_000_000  | 178          | false // total fee = 3382
            1               | 1                | 1.00002383  | 100_000_000  | 110          | true // dustChange = 293
            1               | 1                | 1.00002972  | 100_000_000  | 110          | true // dustChange = 293
            1               | 1                | 1.00002679  | 100_000_000  | 110          | true // total fee = 2679, dustChange = 0
            3               | 2                | 1.00000000  | 250_000_000  | 277          | false // total fee = 5263
            3               | 1                | 1.00001755  | 300_000_000  | 246          | true // total fee = 5263, dustChange = 2
    }

    def "should create transaction with #nInputs inputs and #expectedNOutputs outputs additional test"() {
        given:
            List<Utxo> utxos = createUtxos(inputAmounts)
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
            } else if(!changeIsDust) {
                assert expectedChangeAmount == 0
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

    private List<Utxo> createUtxos(List<BigInteger> amounts) {
        amounts.collect {
            new Utxo(
                    "95ca08e71b5b8149b3c70f363e4a29e6a541850be3f143216d85b4b8429183a1",
                    0,
                    "bcrt1qr8z6uh0pjymq7u70s2xt0vpz5y8kvula632qh6",
                    "",
                    "001419c5ae5de191360f73cf828cb7b022a10f6673fd",
                    Satoshi.toBtc(it),
                    1,
                    null,
                    null
            )
        }
    }

    private List<Utxo> createUtxo(int numberOfUtxos, BigDecimal amount) {
        IntStream.range(0, numberOfUtxos).collect {
            new Utxo(
                    "95ca08e71b5b8149b3c70f363e4a29e6a541850be3f143216d85b4b8429183a1",
                    0,
                    "bcrt1qr8z6uh0pjymq7u70s2xt0vpz5y8kvula632qh6",
                    "",
                    "001419c5ae5de191360f73cf828cb7b022a10f6673fd",
                    amount,
                    1,
                    null,
                    null
            )
        }
    }
}
