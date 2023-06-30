package com.example.budgettracker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

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

    /**
     * Converts a given Calendar object into string in the format of YYYY-MM-DD HH:MM:SS
     * @param calendar Calendar to be converted to string.
     * @return Calendar values in the format of YYYY-MM-DD HH:MM:SS
     */
    public static String getCalendarFormatForDatabase(Calendar calendar){
        String delimiter = "-";
        String hourDelim = ":";
        return calendar.get(Calendar.YEAR) +
                delimiter +
                (calendar.get(Calendar.DAY_OF_MONTH) +
                delimiter +
                calendar.get(Calendar.MONTH)+1) +
                " " +
                calendar.get(Calendar.HOUR) +
                hourDelim +
                calendar.get(Calendar.MINUTE) +
                hourDelim +
                calendar.get(Calendar.SECOND)
                ;
    }

    public static String getCalendarFormatDDMMYYYY(Calendar calendar){
        String delimiter = "/";
        return calendar.get(Calendar.DAY_OF_MONTH)+
                delimiter+
                (calendar.get(Calendar.MONTH)+1)+
                delimiter+
                calendar.get(Calendar.YEAR);
    }

    public static Calendar stringToCalendarDDMMYYYY(String date) throws ParseException {
        String delim = "/";
        String [] dates = date.split(delim);
        Calendar cal = Calendar.getInstance();
        if (dates == null || dates.length < 3){
            return cal;
        }

        cal.set(Calendar.DAY_OF_MONTH, Integer.valueOf(dates[0]));
        cal.set(Calendar.MONTH, Integer.valueOf(dates[1])-1);
        cal.set(Calendar.YEAR, Integer.valueOf(dates[2]));
        return cal;
    }

    /**
     * Converts string to Calendar. String must be in YYYY-MM-DD HH:MM:SS format.
     * @param dbDatetime String to be converted to Calendar.
     * @return Calendar
     * @throws ParseException
     */
    public static Calendar stringToCalendarFromDatabase(String dbDatetime) throws ParseException {
        String datetimeDelimiter = " ";
        String dateDelimiter = "-";
        String timeDelimiter = ":";
        String [] datetime = dbDatetime.split(datetimeDelimiter);
        String [] date = datetime[0].split(dateDelimiter);
        String [] time = datetime[1].split(timeDelimiter);
        Calendar cal = Calendar.getInstance();
        if (datetime == null || datetime.length < 2 || date == null || time == null){
            return cal;
        }

        cal.set(Calendar.YEAR, Integer.valueOf(date[0]));
        cal.set(Calendar.MONTH, Integer.valueOf(date[1])-1);
        cal.set(Calendar.DAY_OF_MONTH, Integer.valueOf(date[2]));
        cal.set(Calendar.HOUR, Integer.valueOf(time[0]));
        cal.set(Calendar.MINUTE, Integer.valueOf(time[1]));
        cal.set(Calendar.SECOND, Integer.valueOf(time[2]));
        return cal;
    }



}
