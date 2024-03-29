package com.byow.wallet.byow.domains.node;

public class ErrorMessages {
    public static final String NOT_ENOUGH_FUNDS = "Could not send transaction: not enough funds.";
    public static final String WRONG_PASSWORD = "Could not send transaction: wrong signature.";
    public static final String WALLET_NOT_LOADED = "Could not send transaction: wallet not loaded.";
    public static final String DUST = "Could not send transaction: amount to send is dust.";
    public static final String WALLET_NAME_ALREADY_EXISTS = "Could not create wallet: the wallet name already exists.";
    public static final String INVALID_WATCH_ONLY_PASSWORD = "Could not load wallet: the wallet password is wrong.";
    public static final String INVALID_ADDRESS = "Could not send transaction: invalid address.";
    public static final String INVALID_TRANSACTION = "Could not send transaction: unexpected transaction content.";
}
