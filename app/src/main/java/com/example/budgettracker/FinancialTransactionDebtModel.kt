package com.example.budgettracker

import java.math.BigDecimal
import java.util.*

data class FinancialTransactionDebtModel(
    var ftdId: String,
    var ftdName: String,
    var ftdAmount: BigDecimal,
    var ftdTransacDate: Calendar
)
