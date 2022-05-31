package com.byow.wallet.byow.domains.node;

import java.util.Map;

public record Error(String message) {
    private static final String DEFAULT_MESSAGE = "An error occurred";

    private static Map<String, String> errorMessages = Map.of(
        "non-mandatory-script-verify-flag (Script failed an OP_EQUALVERIFY operation)", ErrorMessages.WRONG_PASSWORD,
        "dust", ErrorMessages.DUST
    );

    public static Error from(NodeError error) {
        return new Error(errorMessages.getOrDefault(error.message(), DEFAULT_MESSAGE));
    }
}
