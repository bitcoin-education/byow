package com.byow.wallet.byow.api.services;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.List;
import java.util.NoSuchElementException;

import static com.byow.wallet.byow.utils.AddressMatcher.isSegwit;

@Service
public class TransactionSizeCalculator {
    // OVERHEAD
    private static final double N_VERSION = 4;
    private static final double INPUT_COUNT = 1; // for up to 252 inputs
    private static final double OUTPUT_COUNT = 1; // for up to 252 outputs
    private static final double N_LOCKTIME = 4;
    private static final double SEGWIT_MARKER_AND_FLAG = 2; // only for transactions spending segwit UTXOs

    // INPUT
    private static final double OUTPOINT = 36;
    private static final double SCRIPT_SIG_LENGTH = 1; // for up to 252 vbytes
    private static final double N_SEQUENCE = 4;
    private static final double WITNESS_COUNT = 1; // for up to 252 items
    private static final double WITNESS_ITEMS = 107; // for P2WPKH inputs

    // OUTPUT
    private static final double N_VALUE = 8;
    private static final double SCRIPT_PUBKEY_LENGTH = 1; // for up to 252 vbytes
    private static final double SCRIPT_PUBKEY = 22; // for P2WPKH outputs

    private final AddressConfigFinder addressConfigFinder;

    public TransactionSizeCalculator(AddressConfigFinder addressConfigFinder) {
        this.addressConfigFinder = addressConfigFinder;
    }

    public BigInteger calculate(List<String> inputAddresses, List<String> outputAddresses) {
        double overhead = N_VERSION + INPUT_COUNT + OUTPUT_COUNT + N_LOCKTIME + (SEGWIT_MARKER_AND_FLAG / 4);

        double allInputs = inputAddresses.stream()
            .map(this::inputSize)
            .reduce(Double::sum)
            .orElse(0.0);

        double witness = (WITNESS_COUNT + WITNESS_ITEMS) / 4;
        double allWitnesses = inputAddresses.size() * witness;

        double allOutputs = outputAddresses.stream()
            .map(this::outputSize)
            .reduce(Double::sum)
            .orElse(0.0);

        BigDecimal result = BigDecimal.valueOf(overhead + allInputs + allWitnesses + allOutputs);
        result = result.setScale(0, RoundingMode.HALF_UP);
        return result.toBigInteger();
    }

    private double outputSize(String address) {
        return N_VALUE + SCRIPT_PUBKEY_LENGTH + scriptPubkeySize(address);
    }

    private double scriptPubkeySize(String address) {
        if (isSegwit.test(address)) {
            return SCRIPT_PUBKEY;
        }
        throw new NoSuchElementException("Script pubkey size calculation not implemented.");
    }

    private double inputSize(String address) {
        return OUTPOINT + SCRIPT_SIG_LENGTH + addressConfigFinder.findByAddress(address).orElseThrow().scriptSigSize() + N_SEQUENCE;
    }
}
