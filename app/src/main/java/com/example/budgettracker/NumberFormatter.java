package com.example.budgettracker;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class NumberFormatter {

    private String format;

    public NumberFormatter() {
        this.format = "#,##0.00";
    }
    public NumberFormatter(String format) {
        this.format = format;
    }

    public String formatNumber(BigDecimal number) {
        DecimalFormat df = new DecimalFormat(format);
        return df.format(number);
    }

    public String formatNumber(int number) {
        DecimalFormat df = new DecimalFormat(format);
        return df.format(number);
    }

    public void setFormat(String format) {
        this.format = format;
    }
}
