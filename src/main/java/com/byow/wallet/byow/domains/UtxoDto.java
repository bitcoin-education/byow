package com.byow.wallet.byow.domains;

import java.math.BigDecimal;

public record UtxoDto(
    String derivationPath,
    BigDecimal amount
) {
}
