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

class WalletCreateTransactionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallet_create_transaction)

        //For when back gesture or button is triggered will return to Main[financial obj]Activity
        val callback = onBackPressedDispatcher.addCallback(this) {
            setResult(RESULT_OK)
            finish()
        }

        //get all views from layout
        val etWalletTransactionName = findViewById<EditText>(R.id.etWalletTransactionName)
        val etWalletTransactionAmount = findViewById<EditText>(R.id.etWalletTransactionAmount)
        val bWalletCreateTransaction = findViewById<Button>(R.id.bWalletCreateTransaction)

        //Get transaction type and where to link transaction from intent.
        val bundle: Bundle? = intent.extras
        val transactionType = bundle!!.getString("transaction type")
        val walletId = bundle!!.getString("wallet id")

        //Setting the Title
        val title = if (transactionType.equals("add")) "Add Transaction"
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

            if (StringTools.isNullOrEmpty(etWalletTransactionAmount.text.toString())){
                builder.setTitle("Transaction's Amount must not be blank")
                builder.show()
                return@setOnClickListener
            }

            //After validation, get wallet transaction data from edit texts in view.
            val transactionName: String = etWalletTransactionName.text.toString()
            val transactionAmount: BigDecimal = BigDecimal(
                etWalletTransactionAmount.text.toString()
            )

            //Retrieve the child financial object(wallet) where the new transaction will be linked
            val fo: FinancialObject = FileManager.loadFinancialObject(
                applicationContext.filesDir.absolutePath,
                Constants.SAVINGS_FILENAME
            )
            val wallet = fo.childFinancialObjects.get(walletId)

            if(wallet==null){
                Toast.makeText(
                    this,
                    "An Error Occurred: wallet ID: $walletId Does Not Exist",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            //Add Transaction
            if (transactionType.equals("add")){
                wallet.createFinancialTransaction(
                    transactionName,
                    "",
                    transactionAmount
                )
                FileManager.saveFinancialObject(
                    fo,
                    applicationContext.filesDir.absolutePath,
                    Constants.SAVINGS_FILENAME
                )

            }

            //Subtract Transaction
            if(transactionType.equals("sub")){
                if(wallet.financialTransactionsTotal.subtract(transactionAmount).signum() == -1){
                    Toast.makeText(
                        this,
                        "Transaction amount cannot be larger than wallet total",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                wallet.createFinancialTransaction(
                    transactionName,
                    "",
                    transactionAmount.negate()
                )
                FileManager.saveFinancialObject(
                    fo,
                    applicationContext.filesDir.absolutePath,
                    Constants.SAVINGS_FILENAME
                )

            }
            val nextIntent = Intent()
            nextIntent.putExtra("walletId", walletId)
            setResult(RESULT_OK, nextIntent)
            finish()

        }

    }
}