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
