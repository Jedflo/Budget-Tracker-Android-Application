package com.example.budgettracker

import java.math.BigDecimal
import java.util.*

data class FinancialTransactionWalletModel(
    var ftwId: String,
    var ftwName:String,
    var ftwAmount: BigDecimal,
    var ftwTransactionDate: Calendar
    )
