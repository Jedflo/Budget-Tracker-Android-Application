package com.example.budgettracker

import java.text.DecimalFormat

fun prepareDoubleForPrint(double: Double): String {
    val df:DecimalFormat = DecimalFormat("#,##0.00")
    return df.format(double)
}

fun roundToTwoPlaces(double: Double): Double{
    return String.format("%.2f", double).toDouble()
}