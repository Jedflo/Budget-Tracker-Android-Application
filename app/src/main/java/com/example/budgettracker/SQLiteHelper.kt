package com.example.budgettracker

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt

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
        private const val FO_CREATION_DATE = "date_created"

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
                        + FO_ID + " TEXT PRIMARY KEY, "
                        + FO_TYPE + " TEXT, "
                        + FO_NAME + " TEXT, "
                        + FO_DESC + " TEXT, "
                        + FO_TARGET_AMT + " REAL, "
                        + FO_LEVEL + " TEXT, "
                        + FO_CREATION_DATE + " TEXT "
                        + ")"
                )

        val createTransactionsTable = (
                "CREATE TABLE " + TBL_TRANSACTIONS + "("
                        + T_ID + " TEXT PRIMARY KEY, "
                        + T_NAME + " TEXT, "
                        + T_AMOUNT + " REAL, "
                        + T_FO_OBJECT_ID + " TEXT, "
                        + T_DATE + " TEXT, "
                        + T_ATTACHMENT_ID + " TEXT "
                        + ")"
        )

        val createFinancialObjectsRelationTable = (
                "CREATE TABLE " + TBL_FINANCIAL_OBJECT_REL + "("
                        + FOR_PARENT_ID + " TEXT,"
                        + FOR_CHILD_ID + " TEXT"
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
        contentValues.put(FO_CREATION_DATE, CalendarTools.getCalendarFormatForDatabase(fo.dateCreated))

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
        contentValues.put(FO_CREATION_DATE, CalendarTools.getCalendarFormatForDatabase(fo.dateCreated))
        var success = db.insert(TBL_FINANCIAL_OBJECT, null, contentValues)

        contentValues.clear()

        contentValues.put(FOR_PARENT_ID, parentId)
        contentValues.put(FOR_CHILD_ID, fo.id)

        success += db.insert(TBL_FINANCIAL_OBJECT_REL, null, contentValues)

        db.close()
        return success
    }

    /**
     * Updates the database record of a given financial object.
     * @param fo financial object to be updated.
     */
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

    /**
     * Deletes the given financial object from the database.
     * @param fo financial object to be deleted.
     */
    fun deleteFinancialObject(fo: FinancialObjectModel): Int{
        val db = this.writableDatabase
        var success = db.delete(TBL_FINANCIAL_OBJECT, "$FO_ID = ?", arrayOf(fo.id))
        success += db.delete(TBL_FINANCIAL_OBJECT_REL, "$FOR_PARENT_ID = ?", arrayOf(fo.id))
        db.close()
        return success
    }

    /**
     * Fetches all financial objects of a given type from the database.
     * @param financialObjectType type of financial object to be fetched.
     * @return an arraylist of FinancialObjectModels of the given financialObjectType.
     */
    @SuppressLint("Range")
    fun getFinancialObjects(financialObjectType: String): ArrayList<FinancialObjectModel>{
        val financialObjectList: ArrayList<FinancialObjectModel> = ArrayList()
        val db = this.writableDatabase
        val selectQuery = "SELECT * FROM $TBL_FINANCIAL_OBJECT WHERE $FO_TYPE = '$financialObjectType'"
        val cursor: Cursor?

        try {
            cursor = db.rawQuery(selectQuery,null)
        }catch (e: java.lang.Exception){
            e.printStackTrace()
            db.execSQL(selectQuery)
            return ArrayList()
        }

        var id: String
        var type: String
        var name: String
        var description: String
        var targetAmount: Double
        var level: String
        var value: Double
        var creationDateString: String
        var creationDateCalendar: Calendar

        if (cursor.moveToFirst()){
            do {
                id = cursor.getString(cursor.getColumnIndex(FO_ID))
                type = cursor.getString(cursor.getColumnIndex(FO_TYPE))
                name = cursor.getString(cursor.getColumnIndex(FO_NAME))
                description = cursor.getString(cursor.getColumnIndex(FO_DESC))
                targetAmount = cursor.getDouble(cursor.getColumnIndex(FO_TARGET_AMT))
                level = cursor.getString(cursor.getColumnIndex(FO_LEVEL))
                value = getFinancialObjectValue(id)
                creationDateString = cursor.getString(cursor.getColumnIndex(FO_CREATION_DATE))
                creationDateCalendar = CalendarTools.stringToCalendarFromDatabase(creationDateString)
                val financialObject = FinancialObjectModel(
                    id,
                    type,
                    name,
                    description,
                    targetAmount,
                    level,
                    value,
                    creationDateCalendar
                )
                financialObjectList.add(financialObject)
            }while (cursor.moveToNext())
        }
        return financialObjectList
    }

    /**
     * Fetches a specific financial object from the database.
     * @param foID Financial Object ID of financial object to be fetched.
     * @return FinancialObjectModel with the given FO ID.
     */
    @SuppressLint("Range", "Recycle")
    fun getFinancialObject(foID: String): FinancialObjectModel? {
        val db = this.writableDatabase
        val selectQuery = "SELECT * FROM $TBL_FINANCIAL_OBJECT WHERE $FO_ID = '$foID'"
        val cursor: Cursor?

        try {
            cursor = db.rawQuery(selectQuery,null)
            var id: String
            var type: String
            var name: String
            var description: String
            var targetAmount: Double
            var level: String
            var value: Double
            var creationDateString: String
            var creationDateCalendar: Calendar

            if (cursor.moveToFirst()) {
                id = cursor.getString(cursor.getColumnIndex(FO_ID))
                type = cursor.getString(cursor.getColumnIndex(FO_TYPE))
                name = cursor.getString(cursor.getColumnIndex(FO_NAME))
                description = cursor.getString(cursor.getColumnIndex(FO_DESC))
                targetAmount = cursor.getDouble(cursor.getColumnIndex(FO_TARGET_AMT))
                level = cursor.getString(cursor.getColumnIndex(FO_LEVEL))
                value = getFinancialObjectValue(id)
                creationDateString = cursor.getString(cursor.getColumnIndex(FO_CREATION_DATE))
                creationDateCalendar = CalendarTools.stringToCalendarFromDatabase(creationDateString)
                return FinancialObjectModel(
                    id,
                    type,
                    name,
                    description,
                    targetAmount,
                    level,
                    value,
                    creationDateCalendar
                )
            }
        }catch (e: java.lang.Exception){
            e.printStackTrace()
            db.execSQL(selectQuery)

        }
        return null
    }

    fun getFinancialObjectValue(foId: String): Double{
        val db = this.writableDatabase
        val selectQuery = "SELECT SUM($T_AMOUNT) FROM $TBL_TRANSACTIONS WHERE $T_FO_OBJECT_ID = '$foId'"
        val cursor: Cursor?
        var amount = 0.0

        try {
            cursor = db.rawQuery(selectQuery,null)
            if (cursor.moveToFirst()) {
                amount = cursor.getDouble(0)
            }
        }catch (e: java.lang.Exception){
            e.printStackTrace()
            db.execSQL(selectQuery)
        }
        return roundToTwoPlaces(amount)
    }

    fun insertFinancialObjectTransaction(fot: FinancialObjectTransactionModel): Long{
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(T_ID, fot.id)
        contentValues.put(T_NAME, fot.name)
        contentValues.put(T_AMOUNT, fot.amount)
        contentValues.put(T_FO_OBJECT_ID, fot.objectId)
        contentValues.put(T_DATE, CalendarTools.getCalendarFormatForDatabase(fot.transactionDate))
        contentValues.put(T_ATTACHMENT_ID, fot.attachmentId)

        val success = db.insert(TBL_TRANSACTIONS, null, contentValues)
        db.close()
        return success
    }

    @SuppressLint("Range")
    fun getTransactionsOnFinancialObject(foId: String): ArrayList<FinancialObjectTransactionModel>{
        val transactionsList: ArrayList<FinancialObjectTransactionModel> = ArrayList()
        val db = this.writableDatabase
        val selectQuery =
                    "SELECT * " +
                    "FROM $TBL_TRANSACTIONS " +
                    "WHERE $T_FO_OBJECT_ID = '$foId'" +
                    "AND $T_ATTACHMENT_ID IN ('${Const.ATTCH_PARENT}', '${Const.ATTCH_NONE}')"
        val cursor: Cursor?

        try {
            cursor = db.rawQuery(selectQuery,null)
        }catch (e: java.lang.Exception){
            e.printStackTrace()
            db.execSQL(selectQuery)
            return ArrayList()
        }

        var id: String
        var name: String
        var amount: Double
        var financialObjectId: String
        var date: String
        var calendarDate: Calendar
        var attachmentId: String


        if (cursor.moveToFirst()){
            do {
                id = cursor.getString(cursor.getColumnIndex(T_ID))
                name = cursor.getString(cursor.getColumnIndex(T_NAME))
                amount = cursor.getDouble(cursor.getColumnIndex(T_AMOUNT))
                financialObjectId = cursor.getString(cursor.getColumnIndex(T_FO_OBJECT_ID))
                date = cursor.getString(cursor.getColumnIndex(T_DATE))
                calendarDate = CalendarTools.stringToCalendarFromDatabase(date)
                attachmentId = cursor.getString(cursor.getColumnIndex(T_ATTACHMENT_ID))
                val transaction = FinancialObjectTransactionModel(
                    id,
                    name,
                    amount,
                    financialObjectId,
                    calendarDate,
                    attachmentId
                )
                transactionsList.add(transaction)
            }while (cursor.moveToNext())
        }
        return transactionsList
    }

    fun deleteFinancialObjectTransaction(fot: FinancialObjectTransactionModel): Int{
        val db = this.writableDatabase
        val success = db.delete(TBL_TRANSACTIONS, "$T_ID = ?", arrayOf(fot.id))
        db.close()
        return success
    }


}