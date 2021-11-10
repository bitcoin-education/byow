package com.byow.wallet.byow.domains;

import java.util.Date;
import java.util.List;

public record Wallet(String name, List<ExtendedPubkey> extendedPubkeys, Date createdAt) {}
