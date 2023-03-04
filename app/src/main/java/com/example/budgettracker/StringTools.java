package com.example.budgettracker;

import java.text.NumberFormat;
import java.text.ParsePosition;

public class StringTools {
    public static boolean isNullOrEmpty(String string){
        return string==null || string.trim().isEmpty() ? true : false;
    }

    public static boolean isNotNullOrEmpty(String string){
        return string==null || string.trim().isEmpty() ? false : true;
    }

    public static boolean isNumeric(String str) {
        ParsePosition pos = new ParsePosition(0);
        NumberFormat.getInstance().parse(str, pos);
        return str.length() == pos.getIndex();
    }


}
