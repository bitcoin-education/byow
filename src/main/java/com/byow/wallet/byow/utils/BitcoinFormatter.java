package com.byow.wallet.byow.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class BitcoinFormatter {
    public static String format(BigDecimal number) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.ROOT);
        symbols.setDecimalSeparator('.');
        symbols.setGroupingSeparator(',');
        DecimalFormat formatter = new DecimalFormat("#.########", symbols);
        formatter.setGroupingUsed(false);
        formatter.setMinimumFractionDigits(8);
        formatter.setMaximumFractionDigits(8);
        return formatter.format(number.doubleValue());
    }
}
