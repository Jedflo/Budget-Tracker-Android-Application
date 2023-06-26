package com.example.budgettracker

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class SQLiteHelper(context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object{
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "budget_trackers.db"

        //Financial Object Table
        private const val TBL_FINANCIAL_OBJECT = "financial_objects"
        private const val FO_ID = "id"
        private const val FO_TYPE = "type"
        private const val FO_NAME = "name"
        private const val FO_DESC = "description"
        private const val FO_TARGET_AMT = "target_amount"
        private const val FO_LEVEL = "Level" //Parent or Child

        //Transactions Table
        private const val TBL_TRANSACTIONS = "transactions"
        private const val T_ID = "id"
        private const val T_NAME = "name"
        private const val T_AMOUNT = "amount"
        private const val T_FO_OBJECT_ID = "financial_object"
        private const val T_DATE = "date"
        private const val T_ATTACHMENT_ID = "attachment_id"

        //Financial Object Relations Table
        private const val TBL_FINANCIAL_OBJECT_REL = "financial_objects_relation"
        private const val FOR_PARENT_ID = "parent_id"
        private const val FOR_CHILD_ID = "child_id"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createFinancialObjectsTable = (
                "CREATE TABLE " + TBL_FINANCIAL_OBJECT + "("
                        + FO_ID + "TEXT PRIMARY KEY, "
                        + FO_TYPE + "TEXT, "
                        + FO_NAME + "TEXT, "
                        + FO_DESC + "TEXT, "
                        + FO_TARGET_AMT + "REAL, "
                        + FO_LEVEL + "TEXT "
                        + ")"
                )

        val createTransactionsTable = (
                "CREATE TABLE " + TBL_TRANSACTIONS + "("
                        + T_ID + "TEXT PRIMARY KEY, "
                        + T_NAME + "TEXT, "
                        + T_AMOUNT + "REAL, "
                        + T_FO_OBJECT_ID + "TEXT, "
                        + T_DATE + "TEXT, "
                        + T_ATTACHMENT_ID + "TEXT "
                        + ")"
        )

        val createFinancialObjectsRelationTable = (
                "CREATE TABLE " + TBL_FINANCIAL_OBJECT_REL + "("
                        + FOR_PARENT_ID + "TEXT,"
                        + FOR_CHILD_ID + "TEXT"
                        + ")"
                )

        db?.execSQL(createFinancialObjectsTable)
        db?.execSQL(createTransactionsTable)
        db?.execSQL(createFinancialObjectsRelationTable)

    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        TODO("upgrade must fetch the financial object and migrate it to the database.")
    }

    /**
     * Inserts Financial Object to Financial Object Table.
     * @param fo Financial Object to be inserted in database.
     */
    fun insertFinancialObject(fo: FinancialObjectModel): Long {
        val db =this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(FO_ID, fo.id)
        contentValues.put(FO_TYPE, fo.type)
        contentValues.put(FO_NAME, fo.name)
        contentValues.put(FO_DESC, fo.description)
        contentValues.put(FO_TARGET_AMT, fo.targetAmount)
        contentValues.put(FO_LEVEL, fo.level)

        val success = db.insert(TBL_FINANCIAL_OBJECT, null, contentValues)
        db.close()
        return success
    }

    /**
     * Inserts Financial Object to Financial Object Table. The financial object inserted will be a
     * child of the provided parent ID. This method automatically defines the parent child
     * relationship of the provided Financial Object and parent ID.
     * @param fo Financial Object to be inserted in database. Will be a child object of parent id.
     * @param parentId ID of the Financial Object which the new inserted fo will be a child of.
     */
    fun insertFinancialObject(fo: FinancialObjectModel, parentId: String): Long {
        val db =this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(FO_ID, fo.id)
        contentValues.put(FO_TYPE, fo.type)
        contentValues.put(FO_NAME, fo.name)
        contentValues.put(FO_DESC, fo.description)
        contentValues.put(FO_TARGET_AMT, fo.targetAmount)
        contentValues.put(FO_LEVEL, fo.level)
        var success = db.insert(TBL_FINANCIAL_OBJECT, null, contentValues)

        contentValues.clear()

        contentValues.put(FOR_PARENT_ID, parentId)
        contentValues.put(FOR_CHILD_ID, fo.id)

        success += db.insert(TBL_FINANCIAL_OBJECT_REL, null, contentValues)

        db.close()
        return success
    }

    //TODO Create Docs
    fun updateFinancialObject(fo: FinancialObjectModel): Int {
        val db =this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(FO_ID, fo.id)
        contentValues.put(FO_TYPE, fo.type)
        contentValues.put(FO_NAME, fo.name)
        contentValues.put(FO_DESC, fo.description)
        contentValues.put(FO_TARGET_AMT, fo.targetAmount)
        contentValues.put(FO_LEVEL, fo.level)

        var success = db.update(
            TBL_FINANCIAL_OBJECT,
            contentValues,
            "$FO_ID = ?",
            arrayOf(fo.id)
        )
        db.close()
        return success
    }




}