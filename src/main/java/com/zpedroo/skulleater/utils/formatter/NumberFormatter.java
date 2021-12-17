package com.zpedroo.skulleater.utils.formatter;

import org.bukkit.configuration.file.FileConfiguration;

import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.*;

public class NumberFormatter {

    private static NumberFormatter instance;
    public static NumberFormatter getInstance() { return instance; }

    private BigInteger THOUSAND = BigInteger.valueOf(1000);
    private NavigableMap<BigInteger, String> FORMATS = new TreeMap<>();
    private List<String> NAMES = new LinkedList<>();

    public NumberFormatter(FileConfiguration file) {
        instance = this;
        NAMES.addAll(file.getStringList("Number-Formatter"));

        for (int i = 0; i < NAMES.size(); i++) {
            FORMATS.put(THOUSAND.pow(i+1), NAMES.get(i));
        }
    }

    public String formatDecimal(Double number) {
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        formatter.setDecimalFormatSymbols(symbols);
        return formatter.format(number);
    }
}