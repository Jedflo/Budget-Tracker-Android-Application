package com.example.budgettracker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.math.BigDecimal

class DebtAddActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_debt_add)

        val etDebtName = findViewById<EditText>(R.id.etDebtAddDebtName)
        val etDebtDescription = findViewById<EditText>(R.id.etDebtAddDebtDescription)
        val etDebtAmount = findViewById<EditText>(R.id.etDebtAddDebtAmount)
        val bCreatDebt = findViewById<Button>(R.id.bDebtAddCreateDebt)
        bCreatDebt.setOnClickListener {
            //On button click, get the name, description, and debt amount of the debt object to be created.
            val newDebtName = etDebtName.text.toString()
            val newDebtDescription = etDebtDescription.text.toString()

            val newDebtStatus = "Incomplete"

            //Validations
            val builder = AlertDialog.Builder(this)

            builder.setPositiveButton("OK") { dialog, which ->
                dialog.dismiss()
            }

            if (StringTools.isNullOrEmpty(newDebtName)){
                builder.setTitle("Debt Name must not be blank")
                builder.show()
                return@setOnClickListener
            }

            if (StringTools.isNullOrEmpty(etDebtAmount.text.toString())){
                builder.setTitle("Debt Amount must not be blank")
                builder.show()
                return@setOnClickListener
            }
            val newDebtAmount = BigDecimal(etDebtAmount.text.toString())

            //Create the debt object using the data above
            val financialDebtObject = FinancialDebt(
                newDebtName,
                newDebtDescription,
                newDebtStatus,
                newDebtAmount,
                999
            )
            val financialDebtObjectKey = financialDebtObject.financialObjectID

            //Load Main Financial Object to add the created debt object.
            val mainFinancialObject = FileManager.loadFinancialObject(
                applicationContext.filesDir.absolutePath,
                Constants.SAVINGS_FILENAME
            )
            mainFinancialObject.debtObjects.put(financialDebtObjectKey, financialDebtObject)

            //Then save object to android internal storage.
            FileManager.saveFinancialObject(
                mainFinancialObject,
                applicationContext.filesDir.absolutePath,
                Constants.SAVINGS_FILENAME
            )

            Toast.makeText(
                this,
                "$newDebtName, $newDebtDescription, $newDebtAmount",
                Toast.LENGTH_SHORT
            ).show()

            val intent = Intent(applicationContext, MainDebtActivity::class.java)
            intent.putExtra("debtId", financialDebtObjectKey)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            setResult(RESULT_OK)
            finish()

        }


    }
}