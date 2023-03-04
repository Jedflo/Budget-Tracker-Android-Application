package com.example.budgettracker;

public class FinancialCardModel {
    String savingsName;
    String savingsAmount;

    public FinancialCardModel(String savingsName, String savingsAmount) {
        this.savingsName = savingsName;
        this.savingsAmount = savingsAmount;
    }

    public String getSavingsName() {
        return savingsName;
    }

    public void setSavingsName(String savingsName) {
        this.savingsName = savingsName;
    }

    public String getSavingsAmount() {
        return savingsAmount;
    }

    public void setSavingsAmount(String savingsAmount) {
        this.savingsAmount = savingsAmount;
    }
}
