package com.example.budgettracker;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class FinancialObject implements Serializable {

    private final String FinancialObjectID = String.valueOf(IntegerTools.generateRandom(Constants.FINANCIAL_OBJECT_ID_LENGHT));
    private BigDecimal amount;
    private String name;
    private String description = "";

    private String status = "";
    private HashMap<String, FinancialTransaction> transactions = new HashMap<>();
    private HashMap<String, FinancialObject> childFinancialObjects = new HashMap<>();
    private HashMap<String, FinancialDebt> debtObjects = new HashMap<>();
    private HashMap<String, FinancialSavings> savingsObjects = new HashMap<>();

    //=======================================Constructors===========================================
    public FinancialObject() {
        this.amount = BigDecimal.ZERO;
        this.name = "";
        this.description = "";
    }
    public FinancialObject(String name, String description, BigDecimal amount) {
        this.name = name;
        this.description = description;
        this.amount = amount;
    }

    //==========================================Methods=============================================

    //When instantiated, will get the time stamp on time of creation.
    private final Calendar dateCreated = Calendar.getInstance();

    /**
     * Returns the financial object ID of the financial object.
     * @return Financial Object ID
     */
    public String getFinancialObjectID() {
        return FinancialObjectID;
    }

    /**
     * Returns the financial object's amount. Note: the use of amount will depend on what type of
     * financial object will be using it.
     * For Financial Savings it will use this as a goal amount.
     * @return amount
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * Used to set the amount of the Financial Object.
     * @param amount
     */
    public void setAmount(BigDecimal amount) {
        amount = amount.setScale(2, RoundingMode.HALF_UP);
        this.amount = amount;
    }

    /**
     * Returns the name of the financial object.
     * @return financial object name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the financial object
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the description of the financial object.
     * @return description of financial object.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the financial object.
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }


    /**
     * Creates a child financial object for this financial object.
     * @param walletAmount
     * @param walletName
     * @param walletDescription
     * @return the new financial object child.
     * @throws ArithmeticException
     * @throws IllegalArgumentException
     */
    public FinancialObject createChildObject (BigDecimal walletAmount, String walletName, String walletDescription)throws ArithmeticException, IllegalArgumentException{
        FinancialObject wallet = new FinancialObject();
        //Setting Wallet Amount
        amount = amount.subtract(walletAmount);

        if (BigDecimalTools.isNegative(amount)){
            throw new ArithmeticException("wallet amount " + BigDecimalTools.prepareForPrint(walletAmount) + " cannot be greater than salary amount " + BigDecimalTools.prepareForPrint(amount.add(walletAmount)));
        }
        wallet.setAmount(amount);

        //Setting Wallet Name
        if (StringTools.isNullOrEmpty(walletName)){
            throw new IllegalArgumentException("wallet name cannot be blank.");
        }
        wallet.setName(walletName);

        //Setting Wallet Description
        wallet.setDescription(walletDescription);

        childFinancialObjects.putIfAbsent(wallet.FinancialObjectID,wallet);
        return wallet;
    }

    /**
     * Creates a child financial object for this financial object.
     * @param walletAmount
     * @param walletName
     * @param financialTransactionName
     * @param financialTransactionDescription
     * @return The new financial object child.
     * @throws ArithmeticException
     * @throws IllegalArgumentException
     */
    public FinancialObject createChildObject (BigDecimal walletAmount, String walletName, String financialTransactionName, String financialTransactionDescription)throws ArithmeticException, IllegalArgumentException{
        FinancialObject wallet = new FinancialObject();
        //Setting Wallet Amount

        amount = BigDecimalTools.safeSubtract(amount, walletAmount);

        wallet.setAmount(amount);

        //Setting Wallet Name
        if (StringTools.isNullOrEmpty(walletName)){
            throw new IllegalArgumentException("wallet name cannot be blank.");
        }
        wallet.setName(walletName);

        //Setting Wallet Description
        wallet.setDescription("");

        childFinancialObjects.putIfAbsent(wallet.FinancialObjectID,wallet);
        return wallet;
    }

    /**
     * Deletes a specified child object
     * @param childObjectId
     * @return The deleted child object.
     */
    public FinancialObject deleteChildObject(String childObjectId){
        return childFinancialObjects.remove(childObjectId);
    }

    /**
     * Creates a financial transaction for this financial object.
     * @param transactionName
     * @param transactionDescription
     * @param transactionAmount
     */
    public void createFinancialTransaction(String transactionName, String transactionDescription, BigDecimal transactionAmount){
        FinancialTransaction ft = new FinancialTransaction(transactionName, transactionDescription, transactionAmount);
        transactions.put(ft.getFinancialTransactionID(),ft);
    }

    /**
     * Creates a financial transaction for object and returns the created financial transaction.
     * @param transactionName
     * @param transactionDescription
     * @param transactionAmount
     * @return
     */
    public FinancialTransaction createFinancialTransactionAndReturn(String transactionName, String transactionDescription, BigDecimal transactionAmount){
        amount = BigDecimalTools.safeSubtract(amount, transactionAmount);
        FinancialTransaction ft = new FinancialTransaction(transactionName, transactionDescription, transactionAmount);
        transactions.put(ft.getFinancialTransactionID(),ft);
        return ft;
    }


    /**
     * Deletes a specified financial transaction.
     * @param financialTransactionID
     * @return Deleted financial transaction.
     */
    public FinancialTransaction deleteFinancialTransaction(String financialTransactionID){
        return transactions.remove(financialTransactionID);
    }

    /**
     * Creates a Debt object for this financial object.
     * @param debtName
     * @param debtDescription
     * @param debtStatus
     * @param debtAmount
     * @param timesToPay
     */
    public void createDebtObject(String debtName, String debtDescription, String debtStatus, BigDecimal debtAmount, int timesToPay){
        FinancialDebt debtObject = new FinancialDebt(debtName, debtDescription, debtStatus, debtAmount, timesToPay);
        debtObjects.put(debtObject.getFinancialObjectID(), debtObject);
    }

    /**
     * Deletes a specified Debt object
     * @param financialDebtID
     * @return Deleted debt object.
     */
    public FinancialDebt deleteDebtObject(String financialDebtID){
        return debtObjects.remove(financialDebtID);
    }

    /**
     * Creates a Savings object for this financial object.
     * @param savingsName
     * @param savingsDescription
     * @param savingsStatus
     * @param savingsGoalAmount
     */
    public void createSavingsObject(String savingsName, String savingsDescription, String savingsStatus, BigDecimal savingsGoalAmount){
        FinancialSavings savingsObject = new FinancialSavings(savingsName, savingsDescription, savingsStatus, savingsGoalAmount);
        savingsObjects.put(savingsObject.getFinancialObjectID(),savingsObject);
    }

    /**
     * Deletes a specified Savings object
     * @param savingsObjectID
     * @return
     */
    public FinancialSavings deleteSavingsObject(String savingsObjectID){
        return savingsObjects.remove(savingsObjectID);
    }

    /**
     * Returns the total amount of all financial transactions of this object.
     * @return
     */
    public BigDecimal getFinancialTransactionsTotal(){
        BigDecimal out = BigDecimal.ZERO;
        for (Map.Entry<String, FinancialTransaction> entry : transactions.entrySet()) {
            out = out.add(entry.getValue().getAmount());
        }
        return out;
    }

    /**
     * Returns the total amount of all child objects.
     * @return
     */
    public BigDecimal getChildObjectsTotal(){
        BigDecimal out = BigDecimal.ZERO;
        for (Map.Entry<String, FinancialObject> entry : childFinancialObjects.entrySet()) {
            out = out.add(entry.getValue().getAmount());
        }
        return out;
    }

    /**
     * Gets the total of all debt objects.
     * @return
     */
    public BigDecimal getDebtObjectsTotal(){
        BigDecimal out = BigDecimal.ZERO;
        for (Map.Entry<String, FinancialDebt> entry : debtObjects.entrySet()) {
            out = out.add(entry.getValue().getAmount());
        }
        return out;
    }

    /**
     * Gets the total of all savings objects.
     * @return
     */
    public BigDecimal getSavingsObjectsTotal(){
        BigDecimal out = BigDecimal.ZERO;
        for (Map.Entry<String, FinancialSavings> entry : savingsObjects.entrySet()) {
            out = out.add(entry.getValue().getAmount());
        }
        return out;
    }

    /**
     * Returns date when financial object was created
     * @return Date when financial object was created
     */
    public Calendar getDateCreated() {
        return dateCreated;
    }

    /**
     * Returns the financial object's status.
     * @return financial objects status.
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the financial objects status.
     * @param status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Returns all transactions.
     * @return Hashmap of Financial Transactions
     */
    public HashMap<String, FinancialTransaction> getTransactions() {
        return transactions;
    }

    /**
     * Sets the financial transactions of this financial object.
     * @param transactions
     */
    public void setTransactions(HashMap<String, FinancialTransaction> transactions) {
        this.transactions = transactions;
    }

    /**
     * Counts the number of financial transactions.
     * @return
     */
    public int getTransactionsCount(){
        return getTransactions().size();
    }

    /**
     * Returns a hashmap of all child financial objects.
     * @return Hashmap of child financial objects.
     */
    public HashMap<String, FinancialObject> getChildFinancialObjects() {
        return childFinancialObjects;
    }

    /**
     * Sets the child financial objects of this financial object.
     * @param childFinancialObjects
     */
    public void setChildFinancialObjects(HashMap<String, FinancialObject> childFinancialObjects) {
        this.childFinancialObjects = childFinancialObjects;
    }

    /**
     * Returns all the debt objects of this financial object.
     * @return All the debt objects of this financial object.
     */
    public HashMap<String, FinancialDebt> getDebtObjects() {
        return debtObjects;
    }

    /**
     * Sets the debt objects of this financial object.
     * @param debtObjects
     */
    public void setDebtObjects(HashMap<String, FinancialDebt> debtObjects) {
        this.debtObjects = debtObjects;
    }

    /**
     * @return All Savings object of this financial object.
     */
    public HashMap<String, FinancialSavings> getSavingsObjects() {
        return savingsObjects;
    }

    /**
     * Sets the savings objects of this financial object.
     * @param savingsObjects
     */
    public void setSavingsObjects(HashMap<String, FinancialSavings> savingsObjects) {
        this.savingsObjects = savingsObjects;
    }
}
