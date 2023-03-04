package com.example.budgettracker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.math.BigDecimal

class SavingsAddActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_savings)

        val bCreateSavings = findViewById<Button>(R.id.bCreateSavings)
        val etName = findViewById<EditText>(R.id.etName)
        val etDescription = findViewById<EditText>(R.id.etDescription)
        val etGoalAmount = findViewById<EditText>(R.id.etnGoalAmount)
        bCreateSavings.setOnClickListener {
            //On button Click, get the name, description, and goal amount of the savings object to be created.
            val newSavingsName = etName.text.toString()
            val newSavingsDescription = etDescription.text.toString()
            val newSavingsStatus = "Incomplete"

            //Validations
            val builder = AlertDialog.Builder(this)

            builder.setPositiveButton("OK") { dialog, which ->
                dialog.dismiss()
            }

            if (StringTools.isNullOrEmpty(newSavingsName)){
                builder.setTitle("Savings Name must not be blank")
                builder.show()
                return@setOnClickListener
            }

            if(StringTools.isNullOrEmpty(etGoalAmount.text.toString())){
                builder.setTitle("Savings Goal Amount must not be blank")
                builder.show()
                return@setOnClickListener
            }
            val newSavingsGoal = BigDecimal(etGoalAmount.text.toString())

            //Create the savings object using the collected data above
            val financialSavingsObject = FinancialSavings(
                newSavingsName,
                newSavingsDescription,
                newSavingsStatus,
                newSavingsGoal
            )

            val financialSavingsObjectKey = financialSavingsObject.financialObjectID

            //Load Main Financial Object to add the created savings object.
            val mainFinancialObject = FileManager.loadFinancialObject(
                applicationContext.filesDir.absolutePath,
                Constants.SAVINGS_FILENAME
            )
            mainFinancialObject.savingsObjects.put(financialSavingsObjectKey, financialSavingsObject)

            //Save object to android internal storage
            FileManager.saveFinancialObject(
                mainFinancialObject,
                applicationContext.filesDir.absolutePath,
                Constants.SAVINGS_FILENAME
            )

            //send toast to user which confirms that the file has been saved
            Toast.makeText(
                this,
                "$newSavingsName, $newSavingsDescription, $newSavingsGoal",
                Toast.LENGTH_SHORT
            ).show()

//            val intent = Intent(applicationContext, MainActivity::class.java)
//            intent.putExtra(Constants.INTENT_KEY_NAVIGATE_TO, Constants.NAVIGATE_TO_SAVINGS)
            val intent = Intent(applicationContext, MainSavingsActivity::class.java)
            intent.putExtra("id", financialSavingsObjectKey)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            setResult(RESULT_OK)
            finish()
        }

    }
}