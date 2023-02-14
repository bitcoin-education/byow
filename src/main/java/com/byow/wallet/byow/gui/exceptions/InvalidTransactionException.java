package com.byow.wallet.byow.gui.exceptions;

public class InvalidTransactionException extends RuntimeException {
    public InvalidTransactionException(String msg) {
        super(msg);
    }
}
