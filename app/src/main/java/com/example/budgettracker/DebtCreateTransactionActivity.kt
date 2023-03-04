package com.example.budgettracker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.math.BigDecimal

class DebtCreateTransactionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_debt_create_transaction)

        //get All views from layout
        val etDebtTransactionName = findViewById<EditText>(R.id.etDebtTransactionName)
        val etDebtTransactionAmount = findViewById<EditText>(R.id.etDebtTransactionAmount)
        val bDebtCreateTransaction = findViewById<Button>(R.id.bDebtCreateTransaction)

        //Get transaction type data and where to link the transaction from intent
        val bundle : Bundle? = intent.extras
        val transactionType = bundle!!.getString("transaction type")
        val debtId = bundle!!.getString("debtId")
        println("Received Debt Id no. $debtId")

        val title = if (transactionType.equals("add")) "Add Transaction"
        else "Subtract Transaction"
        setTitle(title)

        bDebtCreateTransaction.setOnClickListener {
            //============Validations============//
            val builder = AlertDialog.Builder(this)

            builder.setPositiveButton("OK") { dialog, which ->
                dialog.dismiss()
            }

            if (StringTools.isNullOrEmpty(etDebtTransactionName.text.toString())){
                builder.setTitle("Transaction's Name must not be blank")
                builder.show()
                return@setOnClickListener
            }

            if (StringTools.isNullOrEmpty(etDebtTransactionAmount.text.toString())){
                builder.setTitle("Transaction's Amount must not be blank")
                builder.show()
                return@setOnClickListener
            }

            //Get new savings transaction data from edit texts
            val transactionName: String = etDebtTransactionName.text.toString()
            val transactionAmount: BigDecimal = BigDecimal(etDebtTransactionAmount.text.toString())


            //Retrieve the financial debt object where the new to be created transaction will be linked to.
            val fo: FinancialObject = FileManager.loadFinancialObject(
                applicationContext.filesDir.absolutePath,
                Constants.SAVINGS_FILENAME
            )
            val userDebt: FinancialDebt? = fo.debtObjects.get(debtId)

            if (userDebt==null){
                Toast.makeText(
                    this,
                    "An Error Occurred: Savings ID: " + debtId + "Does not exist",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            //Add Transaction
            if (transactionType.equals("add")){
                userDebt.createFinancialTransaction(
                    transactionName,
                    "",
                    transactionAmount
                )

                if(userDebt.financialTransactionsTotal >= userDebt.amount){
                    userDebt.status = "complete"
                }

                FileManager.saveFinancialObject(
                    fo,
                    applicationContext.filesDir.absolutePath,
                    Constants.SAVINGS_FILENAME
                )
            }

            //Subtract Transaction
            if (transactionType.equals("sub")){
                if(userDebt.financialTransactionsTotal.subtract(transactionAmount).signum() ==
                    -1){
                    Toast.makeText(
                        this,
                        "Transaction amount cannot be larger than savings total",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }
                userDebt.createFinancialTransaction(
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

            val intent = Intent()
            intent.putExtra("debtId", debtId)
            setResult(RESULT_OK,intent)
            finish()

        }


    }








}