package com.byow.wallet.byow.api.services;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.List;

@Service
public class TransactionSizeCalculator {
    // OVERHEAD
    private static double N_VERSION = 4;
    private static double INPUT_COUNT = 1; // for up to 252 inputs
    private static double OUTPUT_COUNT = 1; // for up to 252 outputs
    private static double N_LOCKTIME = 4;
    private static double SEGWIT_MARKER_AND_FLAG = 0.5; // only for transactions spending segwit UTXOs

    // INPUT
    private static double OUTPOINT = 36;
    private static double SCRIPT_SIG_LENGTH = 1; // for up to 252 vbytes
    private static double SCRIPT_SIG = 0; // for segwit inputs
    private static double N_SEQUENCE = 4;
    private static double WITNESS_COUNT = 0.25; // for up to 252 items
    private static double WITNESS_ITEMS = 27; // for P2WPKH inputs

    // OUTPUT
    private static double N_VALUE = 8;
    private static double SCRIPT_PUBKEY_LENGTH = 1; // for up to 252 vbytes
    private static double SCRIPT_PUBKEY = 22; // for P2WPKH outputs


    public BigInteger calculate(List<String> inputAddresses, List<String> outputAddresses) {
        double overhead = N_VERSION + INPUT_COUNT + OUTPUT_COUNT + N_LOCKTIME + SEGWIT_MARKER_AND_FLAG;

        double input = OUTPOINT + SCRIPT_SIG_LENGTH + SCRIPT_SIG + N_SEQUENCE + WITNESS_ITEMS;
        double allInputs = inputAddresses.size() * input;

        double output = N_VALUE + SCRIPT_PUBKEY_LENGTH + SCRIPT_PUBKEY;
        double allOutputs = outputAddresses.size() * output;

        BigDecimal sum = BigDecimal.valueOf(overhead + allInputs + allOutputs);
        sum = sum.setScale(0, RoundingMode.HALF_UP);
        return sum.toBigInteger();
    }
}
