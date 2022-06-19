package com.byow.wallet.byow.utils;

import java.util.function.Predicate;

import static java.util.stream.Stream.of;

public class AddressMatcher {
    public static Predicate<String> isSegwit = address -> of("bcrt", "tb", "bc").anyMatch(address::startsWith);
}
