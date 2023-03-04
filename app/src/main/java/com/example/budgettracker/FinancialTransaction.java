package com.example.budgettracker;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;

public class FinancialTransaction implements Serializable {

    private final String financialTransactionID = String.valueOf(IntegerTools.generateRandom(Constants.FINANCIAL_TRANSACTION_ID_LENGHT));
    private String name;
    private String description;
    private BigDecimal amount;
    private final Calendar dateCreated = Calendar.getInstance();

    private Calendar dateOfTransaction = Calendar.getInstance();

    private String objectOriginId;

    private String objectDestinationId;


    public FinancialTransaction() {
        name = "";
        description = "";
        amount = BigDecimal.ZERO;
    }

    public FinancialTransaction(String name, BigDecimal amount) {
        this.name = name;
        this.amount = amount;
    }

    public FinancialTransaction(String name, String description, BigDecimal amount) {
        this.name = name;
        this.description = description;
        this.amount = amount;
        this.objectDestinationId = objectDestinationId;
    }

    public FinancialTransaction(String name, String description, BigDecimal amount, String objectDestinationId) {
        this.name = name;
        this.description = description;
        this.amount = amount;
        this.objectDestinationId = objectDestinationId;
    }

    public FinancialTransaction(String name, String description, BigDecimal amount, String objectOriginId, String objectDestinationId) {
        this.name = name;
        this.description = description;
        this.amount = amount;
        this.objectOriginId = objectOriginId;
        this.objectDestinationId = objectDestinationId;
    }

    public String getFinancialTransactionID() {
        return financialTransactionID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Calendar getDateCreated() {
        return dateCreated;
    }

    public Calendar getDateOfTransaction() {
        return dateOfTransaction;
    }

    public void setDateOfTransaction(Calendar dateOfTransaction) {
        this.dateOfTransaction = dateOfTransaction;
    }

    public String getObjectOriginId() {
        return objectOriginId;
    }

    public void setObjectOriginId(String objectOriginId) {
        this.objectOriginId = objectOriginId;
    }

    public String getObjectDestinationId() {
        return objectDestinationId;
    }

    public void setObjectDestinationId(String objectDestinationId) {
        this.objectDestinationId = objectDestinationId;
    }
}
