package com.example.budgettracker

import kotlin.streams.asSequence

/**
 * Generates an ID with length idSize. ID generated is alphanumeric
 * @param source What numbers, letters, and symbols the ID can be composed of.
 * @param idSize Length of ID to be generated.
 * @return generated ID.
 */
fun generateAlphaNumericId(idSize: Long): String{
    val source = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
    val out = java.util.Random().ints(idSize, 0, source.length)
        .asSequence().map(source::get).joinToString("")
    return out
}