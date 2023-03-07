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

    /**
     * Converts big decimals into string with provided pattern.
     * @param bigDecimal Big decimal to be converted to string.
     * @param format String pattern of big decimal.
     * @return
     */
    public static String prepareForPrint(BigDecimal bigDecimal, String format){
        DecimalFormat df = new DecimalFormat(format);
        return df.format(bigDecimal);
    }

    /**
     * Converts big decimals into string in #,##0.00 pattern.
     * @param bigDecimal big decimal to be converted to string.
     * @return
     */
    public static String prepareForPrint(BigDecimal bigDecimal){
        DecimalFormat df = new DecimalFormat("#,##0.00");
        return df.format(bigDecimal);
    }

    /**
     * Converts big decimals to string with pattern ###0.00 pattern.
     * @param bigDecimal
     * @return
     */
    public static String prepareForPrintNC(BigDecimal bigDecimal){
        DecimalFormat df = new DecimalFormat("###0.00");
        return df.format(bigDecimal);
    }

    /**
     * Safely subtracts two big decimals
     * i.e., It throws an ArithmeticException when the result is negative.
     * @param bigger
     * @param smaller
     * @return
     * @throws ArithmeticException
     */
    public static BigDecimal safeSubtract(BigDecimal bigger, BigDecimal smaller)
            throws ArithmeticException{
        bigger = bigger.subtract(smaller);
        if (BigDecimalTools.isNegative(bigger)){
            throw new ArithmeticException(
                    "smaller amount " + BigDecimalTools.prepareForPrint(smaller) +
                            " cannot be greater than bigger amount " +
                            BigDecimalTools.prepareForPrint(bigger.add(smaller))
            );
        }
        return bigger;
    }
}
