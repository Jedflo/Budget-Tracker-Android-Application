package com.example.budgettracker

import java.util.*

/**
 * @param id ID of the transaction.
 * @param name name of transaction.
 * @param amount of transaction. can be negative.
 * @param objectId Id of financial object where this transaction is made
 * @param transactionDate date of transaction.
 * @param attachmentId if the transaction is a child object, it will contain the ID of the parent,
 * if the transaction is a parent, it must contain PARENT, else it will contain none.
 */
data class FinancialObjectTransactionModel(
    var id: String,
    var name: String,
    var amount: Double,
    var objectId: String,
    var transactionDate: Calendar,
    var attachmentId: String //Parent, Child, Null
)
