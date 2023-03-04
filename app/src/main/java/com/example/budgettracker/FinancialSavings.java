package com.example.budgettracker;

import java.io.Serializable;
import java.math.BigDecimal;

public class FinancialSavings extends FinancialObject implements Serializable {



    public FinancialSavings(String name, String description, String status, BigDecimal goalAmount) {
        super(name, description, goalAmount);
        setStatus(status);
    }

    @Override
    public void createFinancialTransaction(String transactionName, String transactionDescription, BigDecimal transactionAmount) {
        FinancialTransaction ft = new FinancialTransaction(transactionName, transactionDescription, transactionAmount);
        getTransactions().put(ft.getFinancialTransactionID(),ft);
        System.out.println("SEARCH: "+getTransactionsCount());
    }

}
