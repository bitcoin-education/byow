package com.byow.wallet.byow.domains;

import java.util.List;

public record Wallet(String name, List<ExtendedPubkey> extendedPubkeys) {}
