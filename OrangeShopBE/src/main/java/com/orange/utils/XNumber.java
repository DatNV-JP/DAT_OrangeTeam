package com.orange.utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

public class XNumber {

    public static NumberFormat nf = NumberFormat.getNumberInstance(Locale.forLanguageTag("vi-VN"));

    public static DecimalFormat df = new DecimalFormat("#,##0'đ'");
    public static NumberFormat formatVND() {
        nf.setGroupingUsed(true);
        nf.setMaximumFractionDigits(2);
        nf.setMinimumFractionDigits(2);
        Currency customCurrency = Currency.getInstance("VND");
        nf.setCurrency(customCurrency);
        return nf;
    }
}
