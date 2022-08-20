package com.byow.wallet.byow.api.services;

import com.byow.wallet.byow.utils.Satoshi;
import io.github.bitcoineducation.bitcoinjava.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;

@Service
public class NestedSegwitTransactionSigner implements TransactionSigner {
    @Override
    public void sign(Transaction transaction, PrivateKey privateKey, int index, BigDecimal amount) throws IOException {
        Script redeemScript = Script.p2wpkhScript(Hash160.hashToHex(privateKey.getPublicKey().getCompressedPublicKey()));
        P2SHTransactionECDSASigner.signNestedSegwit(transaction, privateKey, index, redeemScript, Satoshi.toSatoshis(amount));
    }
}
