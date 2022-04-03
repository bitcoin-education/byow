package com.byow.wallet.byow.api.services;

import com.byow.wallet.byow.domains.UtxoDto;
import com.byow.wallet.byow.utils.Satoshi;
import io.github.bitcoineducation.bitcoinjava.*;
import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;
import org.bouncycastle.util.BigIntegers;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.stream.IntStream;

import static io.github.bitcoineducation.bitcoinjava.ExtendedKeyPrefixes.MAINNET_PREFIX;

@Service
public class TransactionSignerService {
    public void sign(Transaction transaction, String mnemonicSeed, String password, List<UtxoDto> utxoDtos) {
        IntStream.range(0, utxoDtos.size())
            .forEachOrdered(i -> sign(i, utxoDtos.get(i), transaction, mnemonicSeed, password));
    }

    private void sign(int i, UtxoDto utxoDto, Transaction transaction, String mnemonicSeedString, String password) {
        MnemonicSeed mnemonicSeed = new MnemonicSeed(mnemonicSeedString);
        ExtendedPrivateKey masterKey = mnemonicSeed.toMasterKey(password, MAINNET_PREFIX.getPrivatePrefix());
        ExtendedPrivateKey extendedPrivateKey = (ExtendedPrivateKey) masterKey.ckd(utxoDto.derivationPath(), true, MAINNET_PREFIX.getPrivatePrefix());
        byte[] keyBytes = ByteUtils.subArray(extendedPrivateKey.getKey(), 0, 32);
        PrivateKey privateKey = new PrivateKey(new BigInteger(1, keyBytes));
        try {
            TransactionECDSASigner.sign(transaction, privateKey, i, Satoshi.toSatoshis(utxoDto.amount()), true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
