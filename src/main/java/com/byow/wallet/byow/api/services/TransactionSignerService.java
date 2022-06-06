package com.byow.wallet.byow.api.services;

import com.byow.wallet.byow.domains.UtxoDto;
import com.byow.wallet.byow.utils.Satoshi;
import io.github.bitcoineducation.bitcoinjava.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.stream.IntStream;

import static com.byow.wallet.byow.domains.AddressType.NESTED_SEGWIT;
import static com.byow.wallet.byow.domains.AddressType.NESTED_SEGWIT_CHANGE;
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
        PrivateKey privateKey = extendedPrivateKey.toPrivateKey();
        try {
            sign(i, utxoDto, transaction, privateKey);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sign(int i, UtxoDto utxoDto, Transaction transaction, PrivateKey privateKey) throws IOException {
        if (List.of(NESTED_SEGWIT, NESTED_SEGWIT_CHANGE).contains(utxoDto.addressType())) {
            Script redeemScript = Script.p2wpkhScript(Hash160.hashToHex(privateKey.getPublicKey().getCompressedPublicKey()));
            P2SHTransactionECDSASigner.signNestedSegwit(transaction, privateKey, i, redeemScript, Satoshi.toSatoshis(utxoDto.amount()));
            return;
        }
        TransactionECDSASigner.sign(transaction, privateKey, i, Satoshi.toSatoshis(utxoDto.amount()), true);
    }
}
