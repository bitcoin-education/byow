package com.byow.wallet.byow.api.services;

import io.github.bitcoineducation.bitcoinjava.MnemonicSeedGenerator;
import org.springframework.stereotype.Service;

@Service
public class MnemonicSeedService {
    public String create() {
        return MnemonicSeedGenerator.generateRandom(256).getSentence();
    }
}
