package com.byow.wallet.byow.domains.node;

import java.util.List;
import java.util.Map;

public record NodeImportDescriptorsResponse(
    boolean success,
    List<String> warnings,
    Map<String, String> error
) {
}
