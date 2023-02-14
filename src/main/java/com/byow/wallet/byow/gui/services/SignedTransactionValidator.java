package com.byow.wallet.byow.gui.services;

import com.byow.wallet.byow.gui.exceptions.InvalidTransactionException;
import io.github.bitcoineducation.bitcoinjava.Transaction;
import io.github.bitcoineducation.bitcoinjava.TransactionInput;
import io.github.bitcoineducation.bitcoinjava.TransactionOutput;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.IntStream;

@Service
public class SignedTransactionValidator {
    public void validateSignedTransaction(Transaction signedTransaction, Transaction unsignedTransaction) {
        validateInputs(signedTransaction.getInputs(), unsignedTransaction.getInputs());
        validateOutputs(signedTransaction.getOutputs(), unsignedTransaction.getOutputs());
    }

    private void validateInputs(ArrayList<TransactionInput> signedTransactionInputs, ArrayList<TransactionInput> unsignedTransactionInputs) {
        if (!equalInputs(signedTransactionInputs, unsignedTransactionInputs)) {
            throw new InvalidTransactionException("Invalid transaction");
        }
    }

    private boolean equalInputs(ArrayList<TransactionInput> signedTransactionInputs, ArrayList<TransactionInput> unsignedTransactionInputs) {
        if (signedTransactionInputs.size() != unsignedTransactionInputs.size()) {
            return false;
        }

        return IntStream.range(0, signedTransactionInputs.size()).mapToObj(i -> {
            if (!signedTransactionInputs.get(i).getPreviousTransactionId().equals(unsignedTransactionInputs.get(i).getPreviousTransactionId())) {
                return false;
            }
            if (!signedTransactionInputs.get(i).getPreviousIndex().equals(unsignedTransactionInputs.get(i).getPreviousIndex())) {
                return false;
            }
            return signedTransactionInputs.get(i).getSequence().equals(unsignedTransactionInputs.get(i).getSequence());
        }).allMatch(aBoolean -> aBoolean);
    }

    private void validateOutputs(ArrayList<TransactionOutput> signedTransactionOutputs, ArrayList<TransactionOutput> unsignedTransactionOutputs) {
        if (!equalOutputs(signedTransactionOutputs, unsignedTransactionOutputs)) {
            throw new InvalidTransactionException("Invalid transaction");
        }
    }

    private boolean equalOutputs(ArrayList<TransactionOutput> signedTransactionOutputs, ArrayList<TransactionOutput> unsignedTransactionOutputs) {
        if (signedTransactionOutputs.size() != unsignedTransactionOutputs.size()) {
            return false;
        }
        return IntStream.range(0, signedTransactionOutputs.size()).mapToObj(i -> {
            if (!signedTransactionOutputs.get(i).getAmount().equals(unsignedTransactionOutputs.get(i).getAmount())) {
                return false;
            }
            try {
                return signedTransactionOutputs.get(i).getScriptPubkey().serialize().equals(unsignedTransactionOutputs.get(i).getScriptPubkey().serialize());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).allMatch(aBoolean -> aBoolean);
    }

}
