package com.example.budgettracker;

import java.util.Calendar;

public class CalendarTools {

    /**
     * This method formats a given calendar date by using the fields provided. The expected input for the variables labeled as 'field' are 'Calendar.DATE', 'Calendar.YEAR', etc...
     * @param calendar Calendar to be converted to string.
     * @param field1
     * @param field2
     * @param field3
     * @param delimiter character to place in between the field values. e.g., '/' will give an output of "10/10/10"
     * @return
     */
    public static String getCalendarFormat(Calendar calendar, int field1, int field2, int field3, String delimiter){
        return calendar.get(field1)+
                delimiter+
                calendar.get(field2)+
                delimiter+
                calendar.get(field3);
    }

    /**
     * This method formats a given calendar date by using the fields provided. The expected input for the variables labeled as 'field' are 'Calendar.DATE', 'Calendar.YEAR', etc...
     * @param calendar Calendar to be converted to string.
     * @param field1
     * @param field2
     * @param field3
     * @return
     */
    public static String getCalendarFormat(Calendar calendar, int field1, int field2, int field3){
        String delimiter = "/";
        return calendar.get(field1)+
                delimiter+
                calendar.get(field2)+
                delimiter+
                calendar.get(field3);
    }

    public static String getCalendarFormatDDMMYYYY(Calendar calendar){
        String delimiter = "/";
        return calendar.get(Calendar.DAY_OF_MONTH)+
                delimiter+
                (calendar.get(Calendar.MONTH)+1)+
                delimiter+
                calendar.get(Calendar.YEAR);
    }
}
