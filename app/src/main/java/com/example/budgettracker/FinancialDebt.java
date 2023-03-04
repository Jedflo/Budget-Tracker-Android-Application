package com.example.budgettracker;

import java.math.BigDecimal;

public class FinancialDebt extends FinancialObject {

    private int timesToPay;

    public FinancialDebt(String name, String description, String status, BigDecimal amount, int timesToPay) {
        super(name, description, amount);
        setStatus(status);
        this.timesToPay = timesToPay;
    }

    public BigDecimal getPaymentAmountDue(){
        return getAmount().divide(new BigDecimal(timesToPay));
    }

    @Override
    public void createFinancialTransaction(String transactionName, String transactionDescription, BigDecimal transactionAmount) {
        FinancialTransaction ft = new FinancialTransaction(transactionName, transactionDescription, transactionAmount);
        getTransactions().put(ft.getFinancialTransactionID(),ft);
    }

    public BigDecimal getUnpaidDebtAmount(){
        BigDecimal debtAmount = getAmount();
        BigDecimal ftTotal = getFinancialTransactionsTotal();
        return debtAmount.subtract(ftTotal);
    }

    public BigDecimal getPaidDebtAmount(){
        return getFinancialTransactionsTotal();
    }

    public int getRemainingPayments(){
        int payments = getTransactionsCount();
        return timesToPay - payments;
    }


    public int getTimesToPay() {
        return timesToPay;
    }

    public void setTimesToPay(int timesToPay) {
        this.timesToPay = timesToPay;
    }
}
