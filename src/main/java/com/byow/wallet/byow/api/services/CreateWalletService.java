package com.byow.wallet.byow.api.services;

import com.byow.wallet.byow.domains.Wallet;
import io.github.bitcoineducation.bitcoinjava.ExtendedKey;
import io.github.bitcoineducation.bitcoinjava.ExtendedPrivateKey;
import io.github.bitcoineducation.bitcoinjava.MnemonicSeed;
import org.springframework.stereotype.Component;

import java.math.BigInteger;

import static io.github.bitcoineducation.bitcoinjava.AddressConstants.MAINNET_P2WPKH_ADDRESS_PREFIX;

@Component
public class CreateWalletService {
    public Wallet create(String name, String password, String mnemonicSeedString) {
        MnemonicSeed mnemonicSeed = new MnemonicSeed(mnemonicSeedString);
        ExtendedPrivateKey masterKey = mnemonicSeed.toMasterKey(password, "mainnet"); //TODO: tirar segundo parametro do metodo no bitcoinjava
        ExtendedKey extendedPubKey = masterKey.ckd("84'/0'/0'/0", false);
        ExtendedKey extendedPubKeyFirst = extendedPubKey.ckd(BigInteger.ZERO, false, false);
        String address = extendedPubKeyFirst.toPublicKey().segwitAddressFromCompressedPublicKey(MAINNET_P2WPKH_ADDRESS_PREFIX);
        System.out.println(address);
        return new Wallet(name);
    }
}
