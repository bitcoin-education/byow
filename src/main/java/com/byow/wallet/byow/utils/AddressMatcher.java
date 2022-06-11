package com.byow.wallet.byow.utils;

import java.util.function.Predicate;

import static java.util.stream.Stream.of;

public class AddressMatcher {
    public static Predicate<String> isSegwit = address -> of("bcrt", "tb", "bc").anyMatch(address::startsWith);

    public static Predicate<String> isNestedSegwit = address -> of("3", "2").anyMatch(address::startsWith);
}
