package com.example.budgettracker

import android.content.Intent

class Const {
    companion object {
        const val LEVEL_PARENT = "parent"
        const val LEVEL_CHILD = "child"
        const val LEVEL_NULL = "none"
        const val FO_TYPE_SAVINGS = "savings"
        const val FO_TYPE_WALLET = "wallet"
        const val FO_TYPE_DEBT = "debt"
        const val FO_TYPE_BORROWED = "borrowed"
        const val INTENT_KEY_WALLET_ID = "wallet id"
        const val INTENT_KEY_TRANSACTION_TYPE = "transaction type"
        const val INTENT_VALUE_ADD_TRANSACTION = "add"
        const val INTENT_VALUE_SUB_TRANSACTION = "sub"
        const val ATTCH_PARENT = "parent"
        const val ATTCH_NONE = "no attachment"
    }
}