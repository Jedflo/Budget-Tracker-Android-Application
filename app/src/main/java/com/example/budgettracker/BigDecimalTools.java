package com.example.budgettracker;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class BigDecimalTools {

    public static boolean isNegative(BigDecimal bigDecimal){
        return (bigDecimal.compareTo(BigDecimal.ZERO)<0) ? true : false;
    }

    public static boolean isNotNegative(BigDecimal bigDecimal){
        return (bigDecimal.compareTo(BigDecimal.ZERO)>0) ? true : false;
    }

    public static String prepareForPrint(BigDecimal bigDecimal){
        DecimalFormat df = new DecimalFormat("#,##0.00");
        return df.format(bigDecimal);
    }

    public static String prepareForPrintNC(BigDecimal bigDecimal){
        DecimalFormat df = new DecimalFormat("###0.00");
        return df.format(bigDecimal);
    }

    public static BigDecimal safeSubtract(BigDecimal amount, BigDecimal walletAmount) throws ArithmeticException{
        amount = amount.subtract(walletAmount);
        if (BigDecimalTools.isNegative(amount)){
            throw new ArithmeticException("wallet amount " + BigDecimalTools.prepareForPrint(walletAmount) + " cannot be greater than salary amount " + BigDecimalTools.prepareForPrint(amount.add(walletAmount)));
        }
        return amount;
    }
}
