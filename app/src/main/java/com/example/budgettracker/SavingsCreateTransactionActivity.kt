package com.example.budgettracker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.math.BigDecimal

class SavingsCreateTransactionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_savings_create_transaction)

        val etSavingsTransactionName = findViewById<EditText>(R.id.etSavingsTransactionName)
        val etSavingsTransactionAmount = findViewById<EditText>(R.id.etSavingsTransactionAmount)
        val bSavingsCreateTransaction = findViewById<Button>(R.id.bSavingsCreateTransaction)

        bSavingsCreateTransaction.setOnClickListener {
            //Get new savings transaction data from edit texts
            val transactionName: String = etSavingsTransactionName.text.toString()
            val transactionAmount: BigDecimal = BigDecimal(etSavingsTransactionAmount.text.toString())

            //Get transaction type data and where to link the transaction from intent
            val bundle : Bundle? = intent.extras
            val transactionType = bundle!!.getString("transaction type")
            val savingsId = bundle!!.getString("id")

            //Retrieve the financial savings object where the new to be created transaction will be linked to.
            val fo: FinancialObject = FileManager.loadFinancialObject(applicationContext.filesDir.absolutePath,Constants.SAVINGS_FILENAME)
            val userSavings: FinancialSavings? = fo.savingsObjects.get(savingsId)

            if (userSavings==null){
                Toast.makeText(
                    this,
                    "An Error Occurred: Savings ID: " + savingsId + "Does not exist",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            //Add Transaction
            if (transactionType.equals("add")){
                userSavings.createFinancialTransaction(
                    transactionName,
                    "",
                    transactionAmount
                )

                if(userSavings.financialTransactionsTotal >= userSavings.amount){
                    userSavings.status = "complete"
                }

                FileManager.saveFinancialObject(
                    fo,
                    applicationContext.filesDir.absolutePath,
                    Constants.SAVINGS_FILENAME
                )
            }
            //Subtract Transaction
            if (transactionType.equals("sub")){
                if(userSavings.financialTransactionsTotal.subtract(transactionAmount).signum() ==
                    -1){
                    Toast.makeText(
                        this,
                        "Transaction amount cannot be larger than savings total",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }
                userSavings.createFinancialTransaction(
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
//
//            val intent = Intent(this, MainSavingsActivity::class.java)
//            intent.putExtra("id", savingsId)
//            intent.putExtra("amount", userSavings.financialTransactionsTotal) //BigDecimal, must fetch as BD and convert to string in next activity.
//            startActivity(intent)
//            finish()
            val intent = Intent()
            intent.putExtra("id", savingsId)
            setResult(RESULT_OK, intent)
            finish()

        }


    }
}