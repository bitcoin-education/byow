package com.byow.wallet.byow.api.services;

import com.byow.wallet.byow.domains.UtxoDto;
import io.github.bitcoineducation.bitcoinjava.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.IntStream;

import static io.github.bitcoineducation.bitcoinjava.ExtendedKeyPrefixes.MAINNET_PREFIX;

@Service
public class TransactionSignerService {
    private final AddressConfigFinder addressConfigFinder;

    public TransactionSignerService(AddressConfigFinder addressConfigFinder) {
        this.addressConfigFinder = addressConfigFinder;
    }

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
        TransactionSigner transactionSigner = addressConfigFinder.findByAddressType(utxoDto.addressType().toString()).transactionSigner();
        transactionSigner.sign(transaction, privateKey, i, utxoDto.amount());
    }
}
