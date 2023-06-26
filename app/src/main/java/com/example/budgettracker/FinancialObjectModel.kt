package com.example.budgettracker


/**
 * @param id ID of financial object.
 * @param type type of financial object. can be Savings, Wallet, Debts, Borrowed.
 * @param name name of financial object.
 * @param description description of financial object.
 * @param targetAmount target amount of financial object. if wallet amount is 0.
 * @param level determines if a financial object is parent or child. this relationship is defined
 * by the Financial Objects Relation Table.
 */
data class FinancialObjectModel(
    var id: String,
    var type: String,
    var name: String,
    var description: String,
    var targetAmount: Double,
    var level: String
)
