package com.byow.wallet.byow.api.services;

import io.github.bitcoineducation.bitcoinjava.MnemonicSeedGenerator;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;

@Service
public class MnemonicSeedService {

    public String create() throws FileNotFoundException {
        return MnemonicSeedGenerator.generateRandom(256).getSentence();
    }
}
