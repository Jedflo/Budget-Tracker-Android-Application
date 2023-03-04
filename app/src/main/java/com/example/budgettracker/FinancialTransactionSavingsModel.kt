package com.example.budgettracker

import java.math.BigDecimal
import java.util.*

data class FinancialTransactionSavingsModel(
    var ftsId: String,
    var ftsName: String,
    var ftsAmount: BigDecimal,
    var ftsTransacDate: Calendar
    )