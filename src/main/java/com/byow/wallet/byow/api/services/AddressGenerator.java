package com.byow.wallet.byow.api.services;

import io.github.bitcoineducation.bitcoinjava.ExtendedKey;

public interface AddressGenerator {
    String generate(ExtendedKey extendedChildKey);
}
