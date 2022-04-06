package com.byow.wallet.byow.api

import com.byow.wallet.byow.api.services.DustCalculator
import com.byow.wallet.byow.api.services.SingleRandomDrawCoinSelector
import com.byow.wallet.byow.api.services.TransactionSizeCalculator
import com.byow.wallet.byow.domains.Utxo
import spock.lang.Specification

import java.util.stream.IntStream

class SingleRandomDrawCoinSelectorTest extends Specification {
    SingleRandomDrawCoinSelector singleRandomDrawCoinSelector

    def setup() {
        singleRandomDrawCoinSelector = new SingleRandomDrawCoinSelector(new TransactionSizeCalculator(), new DustCalculator(3000))
    }

    def "should select #expectedNInputs coins for transaction with #expectedNOutputs outputs"() {
        given:
            List<Utxo> utxos = createUtxo(5, inputAmount)
            String addressToSend = "bcrt1q3d5nn9qw9s44cr6g6mh75m0cf4tr7prsfrm5c6"
            String changeAddress = "bcrt1qgykwpz3ql696ct5denuqavk7xcmh6lzwmpcyuk"
            BigDecimal feeRate = 0.0002
        when:
            List<Utxo> selectedUtxos = singleRandomDrawCoinSelector.select(utxos, amountToSend, feeRate, addressToSend, changeAddress)
        then:
            selectedUtxos.size() == expectedNInputs
        where:
            expectedNInputs | expectedNOutputs | inputAmount | amountToSend
            1               | 2                | 1.00000000  | 50_000_000
            1               | 1                | 1.00002090  | 100_000_000 // total fee = 2090
            1               | 1                | 1.00002383  | 100_000_000 // total fee = 2090, dustChange = 293
            1               | 1                | 1.00002384  | 100_000_000 // total fee = 2090, change = 294 (fee insufficient for 2 outputs)
            2               | 2                | 1.00000000  | 150_000_000 // total fee = 3971
            1               | 2                | 1.00002679  | 100_000_000 // total fee = 2679
            2               | 1                | 1.00001691  | 200_000_000 // total fee = 3382
            3               | 2                | 1.00000000  | 250_000_000 // total fee = 5263
            3               | 1                | 1.00001755  | 300_000_000 // total fee = 5263, dustChange = 2
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
