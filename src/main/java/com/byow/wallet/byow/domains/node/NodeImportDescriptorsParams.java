package com.byow.wallet.byow.domains.node;

import java.util.List;

public record NodeImportDescriptorsParams(
    String desc,
    boolean active,
    List<Integer> range,
    int next_index,
    long timestamp
) {
}
