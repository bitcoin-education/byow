package com.byow.wallet.byow.domains.node;

import java.util.List;

public record NodeFee(Double feerate, List<String> errors) {
}
