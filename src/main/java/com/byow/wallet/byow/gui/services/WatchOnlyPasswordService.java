package com.byow.wallet.byow.gui.services;

import com.byow.wallet.byow.database.entities.WalletEntity;
import io.github.bitcoineducation.bitcoinjava.Sha256;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.stereotype.Service;

import javax.security.auth.login.LoginException;

@Service
public class WatchOnlyPasswordService {
    public void validate(WalletEntity walletEntity, String password) throws LoginException {
        String encryptedPassword = walletEntity.getWatchOnlyPassword().getPassword();
        if (!encrypt(password).equals(encryptedPassword)) {
            throw new LoginException("Wrong password.");
        }
    }

    public String encrypt(String password) {
        return Sha256.hashToHex(Hex.toHexString(password.getBytes()));
    }
}
