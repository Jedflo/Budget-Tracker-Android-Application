package com.example.budgettracker

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.math.BigDecimal
import java.util.Calendar
import kotlin.math.roundToInt
import kotlin.streams.asSequence

class WalletCreateTransactionActivity : AppCompatActivity() {
    private lateinit var etWalletTransactionName: EditText
    private lateinit var etWalletTransactionAmount: EditText
    private lateinit var bWalletCreateTransaction: Button
    private lateinit var sqlLiteHelper: SQLiteHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallet_create_transaction)
        initViews()
        sqlLiteHelper = SQLiteHelper(this)

        //For when back gesture or button is triggered will return to Main[financial obj]Activity
        val callback = onBackPressedDispatcher.addCallback(this) {
            setResult(RESULT_OK)
            finish()
        }

        //Get transaction type and where to link transaction from intent.
        val bundle: Bundle? = intent.extras
        val transactionType = bundle!!.getString(Const.INTENT_KEY_TRANSACTION_TYPE)
        val walletId = bundle!!.getString(Const.INTENT_KEY_WALLET_ID)

        if (walletId.isNullOrEmpty()){
            val builder = AlertDialog.Builder(this)
            builder.setPositiveButton("OK"){dialog, which ->
                dialog.dismiss()
                setResult(RESULT_OK)
                finish()
            }
            builder.setTitle("Wallet ID could not be found.")
            builder.show()
        }

        //Setting the Title
        val title = if (transactionType.equals(Const.INTENT_VALUE_ADD_TRANSACTION)) "Add Transaction"
        else "Subtract Transaction"
        setTitle(title)

        //Create Transaction Button Functionality
        bWalletCreateTransaction.setOnClickListener {
            //============Validations============//

            val builder = AlertDialog.Builder(this)

            builder.setPositiveButton("OK") { dialog, which ->
                dialog.dismiss()
            }

            if (StringTools.isNullOrEmpty(etWalletTransactionName.text.toString())){
                builder.setTitle("Transaction's Name must not be blank")
                builder.show()
                return@setOnClickListener
            }

            if (StringTools.isNullOrEmpty(etWalletTransactionAmount.text.toString()
                    .replace(",",""))){
                builder.setTitle("Transaction's Amount must not be blank")
                builder.show()
                return@setOnClickListener
            }
            //============Validations============//
            //After validation, get wallet transaction data from edit texts in view.
            val transactionName: String = etWalletTransactionName.text.toString()
            val transactionAmount: Double = etWalletTransactionAmount.text.toString()
                .replace(",","").toDouble()


            //Check if wallet ID is an actual Financial Object within the database.
            val wallet = sqlLiteHelper.getFinancialObject(walletId!!)
            if(wallet==null){
                Toast.makeText(
                    this,
                    "An Error Occurred: wallet ID: $walletId Does Not Exist",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            //Add Transaction
            if (transactionType.equals(Const.INTENT_VALUE_ADD_TRANSACTION)){
                val transactionId = generateAlphaNumericId(16)
                val newTransaction = FinancialObjectTransactionModel(
                    transactionId,
                    transactionName,
                    transactionAmount,
                    walletId,
                    Calendar.getInstance(),
                    Const.ATTCH_NONE
                )

                sqlLiteHelper.insertFinancialObjectTransaction(newTransaction)
            }

            //Subtract Transaction
            if(transactionType.equals(Const.INTENT_VALUE_SUB_TRANSACTION)){

                if (wallet.valueAmount < transactionAmount){
                    Toast.makeText(
                        this,
                        "Transaction amount cannot be larger than wallet total",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                val transactionId = generateAlphaNumericId(16)
                val newTransaction = FinancialObjectTransactionModel(
                    transactionId,
                    transactionName,
                    -transactionAmount,//negated because transaction is subtraction.
                    walletId,
                    Calendar.getInstance(),
                    Const.ATTCH_NONE
                )

                sqlLiteHelper.insertFinancialObjectTransaction(newTransaction)
            }
            val nextIntent = Intent()
            nextIntent.putExtra("walletId", walletId)
            setResult(RESULT_OK, nextIntent)
            finish()

        }

    }

    /**
     * Initiate all views from layout.
     */
    private fun initViews(){
        etWalletTransactionName = findViewById(R.id.etWalletTransactionName)
        etWalletTransactionAmount = findViewById(R.id.etWalletTransactionAmount)
        etWalletTransactionAmount.addTextChangedListener(NumberTextWatcher(etWalletTransactionAmount))
        bWalletCreateTransaction = findViewById(R.id.bWalletCreateTransaction)
    }


}