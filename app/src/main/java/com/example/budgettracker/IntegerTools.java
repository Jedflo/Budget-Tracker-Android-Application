package com.example.budgettracker;

import java.util.HashMap;
import java.util.Random;

public class IntegerTools {

    /**
     * Method that test how many iterations an ID with the provided length will have a duplicate.
     * @param length
     * @return
     */
    public static int generateIDDuplicateTest(int length){
        HashMap<Long,String> map = new HashMap<>();
        boolean loop = true;

        while (loop){
            long id = generateRandom(length);
            String string = (String) map.putIfAbsent(id, "present");
            if (StringTools.isNotNullOrEmpty(string)){
                break;
            }
        }
        return (map.size()+1);
    }

    /**
     * Method that gets the average of
     * @param iterations
     * @param lenght
     */
    public static void generateIDDuplicateTestAverage(int iterations, int lenght){
        int total = 0;
        NumberFormatter numberFormatter = new NumberFormatter("#,###");
        for (int i = 0; iterations>i; i++){
            System.out.println("duplicate at: " + numberFormatter.formatNumber(generateIDDuplicateTest(lenght)));
            total = total + generateIDDuplicateTest(lenght);

        }
        System.out.println("Total: " + numberFormatter.formatNumber(total) + " / " + " Iterations: " + numberFormatter.formatNumber(iterations) );
        System.out.println("Average = " + numberFormatter.formatNumber(total/iterations));
    }

    public static long generateRandom(int length) {
        Random random = new Random();
        char[] digits = new char[length];
        digits[0] = (char) (random.nextInt(9) + '1');
        for (int i = 1; i < length; i++) {
            digits[i] = (char) (random.nextInt(10) + '0');
        }
        return Long.parseLong(new String(digits));
    }



}
