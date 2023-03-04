package com.example.budgettracker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class FileManager {

    /**
     * Writes an object to a file. If a file with the same filename already exist, this method overwrites it.
     * @param directory directory where the file will be written.
     * @param fileName file name of the file to be written.
     * @param object object that will be stored within the file.
     * @return true, if the object was saved successfully, otherwise false.
     */
    public static Boolean write(String directory, String fileName, Object object){
        Boolean out = true;
        try {
            File dir = new File(directory);
            if (! dir.exists()){
                dir.mkdir();
            }

            File file = new File(directory + "/" + fileName);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);

            objectOutputStream.writeObject(object);

            fileOutputStream.close();
            objectOutputStream.close();
        } catch (IOException e) {
            out=false;
            e.printStackTrace();
        }

        return out;
    }

    /**
     * Reads a file and retrieves an object from it.
     * @param directory directory of the file to be read.
     * @param fileName file name of the file to be read.
     * @return Object retrieved from file.
     */
    public static Object read(String directory, String fileName){
        Object object = null;
        try {
            File file = new File(directory + "/" + fileName);
            FileInputStream fileInputStream = new FileInputStream(file);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            object = objectInputStream.readObject();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return object;
    }

    public static HashMap readMapFromFile(String directory, String fileName){
        HashMap<String, FinancialSavings> savingsMap = null;
        try {
            File file = new File(directory + "/" + fileName);
            FileInputStream fileInputStream = new FileInputStream(file);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            savingsMap = (HashMap<String, FinancialSavings>)objectInputStream.readObject();

        } catch (ClassNotFoundException | IOException e) {
            return null;
        }

        return savingsMap;
    }


    public static Boolean delete(String directory, String fileName){
        File myObj = new File(directory + "/" + fileName);
        if (myObj.delete()) {
            return true;
        } else {
            return false;
        }
    }


    public static boolean saveSavings(FinancialSavings financialSavings, String directory, String fileName){
        boolean out;
        //Retrieve existing hashmap from file, if any.
        Map <String, FinancialSavings>savingsMap = readMapFromFile(directory,fileName);

        //Check if retrieved hashmap is null. if null probably means that there is no file yet. so we create a new hashmap.
        if (savingsMap==null){
            savingsMap = new HashMap<>();
        }

        //Delete existing file. in the case that there is no existing file it will skip this step.
        delete(directory, fileName);

        //add the new financialSavings object to hashmap
        savingsMap.put(financialSavings.getFinancialObjectID(), financialSavings);

        //write new hashmap to file.
        out = write(directory, fileName, savingsMap);

        return out;
    }

    public static boolean saveDebt(FinancialDebt financialDebt, String directory, String fileName){
        boolean out = false;
        Map <String, FinancialDebt>savingsMap = new HashMap<>();
        try {
            File file = new File(directory + "/" + fileName);
            FileInputStream fileInputStream = new FileInputStream(file);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            delete(directory, fileName);
            savingsMap.put(financialDebt.getFinancialObjectID(), financialDebt);
            write(directory, fileName, savingsMap);
            out=true;

        } catch (FileNotFoundException e) {
            savingsMap.put(financialDebt.getFinancialObjectID(), financialDebt);
            write(directory, fileName, savingsMap);
            out=true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return out;
    }

    public static boolean saveFinancialObject(FinancialObject financialObject, String directory, String fileName){
        boolean out = true;
        //Retrieve Financial Object from file, if any.
        FinancialObject fo = loadFinancialObject(directory,fileName);

        //Check if retrieved FinancialObject is null. if null probably means that there is no file yet. so we create a FinancialObject.
        if (fo==null){
            financialObject = new FinancialObject();
        }

        //Delete existing file. in the case that there is no existing file it will skip this step.
        delete(directory, fileName);

        //write new FinancialObject to file.
        out = write(directory, fileName, financialObject);

        return out;
    }

    public static FinancialObject loadFinancialObject(String directory, String fileName){
        FinancialObject retrievedFinancialObject = null;
        try {
            File file = new File(directory + "/" + fileName);
            FileInputStream fileInputStream = new FileInputStream(file);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            retrievedFinancialObject = (FinancialObject)objectInputStream.readObject();

        } catch (ClassNotFoundException | IOException e) {
            return null;
        }

        return retrievedFinancialObject;
    }




    public static void main(String[] args) {

        String dir = "D:\\Jed\\New folder\\save files";
        String fname = ".fno";

        FinancialObject fo = new FinancialObject("testing", "created for testing", BigDecimal.TEN);
        System.out.println(fo.getFinancialObjectID());
        fname = fo.getFinancialObjectID()+fname;
        write(dir, fname, fo);

        FinancialObject retrievedFo = (FinancialObject) read(dir, fname);

        if (retrievedFo == null){
            System.out.println("file could not be found!");
        }else {
            System.out.println(retrievedFo.getFinancialObjectID());
        }

//        System.out.println(delete(dir, "19467360.fno"));
//        System.out.println(delete(dir, "19467360.fno"));

    }

}
