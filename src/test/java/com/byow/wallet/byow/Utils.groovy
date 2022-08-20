package com.byow.wallet.byow

import com.byow.wallet.byow.domains.Utxo
import com.byow.wallet.byow.utils.Satoshi

class Utils {
    static List<Utxo> createUtxos(List<BigInteger> amounts) {
        amounts.collect { createUtxo(it) }
    }

    static Utxo createUtxo(BigInteger amount) {
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
