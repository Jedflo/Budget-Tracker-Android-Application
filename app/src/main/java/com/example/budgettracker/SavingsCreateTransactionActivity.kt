package com.example.budgettracker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.math.BigDecimal
import java.util.*

class SavingsCreateTransactionActivity : AppCompatActivity() {

    private lateinit var etSavingsTransactionName: EditText
    private lateinit var etSavingsTransactionAmount: EditText
    private lateinit var bSavingsCreateTransaction: Button
    private lateinit var sqLiteHelper: SQLiteHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_savings_create_transaction)

        //Initiate all views.
        initViews()
        sqLiteHelper = SQLiteHelper(this)

        //For when back gesture or button is triggered will return to Main[financial obj]Activity
        val callback = onBackPressedDispatcher.addCallback(this) {
            setResult(RESULT_OK)
            finish()
        }

        //Get transaction type data and where to link the transaction from intent
        val bundle : Bundle? = intent.extras
        val transactionType = bundle!!.getString(Const.INTENT_KEY_TRANSACTION_TYPE)
        val savingsId = bundle!!.getString(Const.INTENT_KEY_SAVINGS_ID)

        //Message if no savings id is retrieved from bundle.
        if (savingsId.isNullOrEmpty()){
            val builder = AlertDialog.Builder(this)
            builder.setPositiveButton("OK"){dialog, which ->
                dialog.dismiss()
                setResult(RESULT_OK)
                finish()
            }
            builder.setTitle("Savings ID could not be found.")
            builder.show()
        }

        //Setting Title
        val title = if (transactionType.equals(Const.INTENT_VALUE_ADD_TRANSACTION)) "Add Transaction"
        else "Subtract Transaction"
        setTitle(title)

        //Transaction buttons functionality.
        bSavingsCreateTransaction.setOnClickListener {
            //============Validations============//

            val builder = AlertDialog.Builder(this)

            builder.setPositiveButton("OK") { dialog, which ->
                dialog.dismiss()
            }

            if (StringTools.isNullOrEmpty(etSavingsTransactionName.text.toString())){
                builder.setTitle("Transaction's Name must not be blank")
                builder.show()
                return@setOnClickListener
            }

            if (StringTools.isNullOrEmpty(etSavingsTransactionAmount.text.toString()
                    .replace(",",""))){
                builder.setTitle("Transaction's Amount must not be blank")
                builder.show()
                return@setOnClickListener
            }
            //============Validations============//
            //Get new savings transaction data from edit texts
            val transactionName: String = etSavingsTransactionName.text.toString()
            var transactionAmount:Double = etSavingsTransactionAmount.text.toString()
                .replace(",","").toDouble()

            //Check if savings ID is an actual Financial Object within the database.
            val userSavings = sqLiteHelper.getFinancialObject(savingsId!!)
            if(userSavings==null){
                Toast.makeText(
                    this,
                    "An Error Occurred: wallet ID: $savingsId Does Not Exist",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val transactionId = generateAlphaNumericId(16)
            //Add Transaction
            if (transactionType.equals(Const.INTENT_VALUE_ADD_TRANSACTION)){

                val newTransaction = FinancialObjectTransactionModel(
                    transactionId,
                    transactionName,
                    transactionAmount,
                    savingsId,
                    Calendar.getInstance(),
                    Const.ATTCH_NONE
                )

                sqLiteHelper.insertFinancialObjectTransaction(newTransaction)
            }

            //Subtract Transaction
            if (transactionType.equals(Const.INTENT_VALUE_SUB_TRANSACTION)){
                if(userSavings.valueAmount < transactionAmount){
                    Toast.makeText(
                        this,
                        "Transaction amount cannot be larger than savings total",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                val newTransaction = FinancialObjectTransactionModel(
                    transactionId,
                    transactionName,
                    -transactionAmount,
                    savingsId,
                    Calendar.getInstance(),
                    Const.ATTCH_NONE
                )

                sqLiteHelper.insertFinancialObjectTransaction(newTransaction)

            }
            val intent = Intent()
            intent.putExtra(Const.INTENT_KEY_WALLET_ID, savingsId)
            setResult(RESULT_OK, intent)
            finish()

        }


    }

    private fun initViews() {
        etSavingsTransactionName = findViewById(R.id.etSavingsTransactionName)
        etSavingsTransactionAmount = findViewById(R.id.etSavingsTransactionAmount)
        etSavingsTransactionAmount.addTextChangedListener(NumberTextWatcher(etSavingsTransactionAmount))
        bSavingsCreateTransaction = findViewById(R.id.bSavingsCreateTransaction)
    }
}