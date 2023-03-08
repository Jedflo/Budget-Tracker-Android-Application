package com.example.budgettracker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import java.math.BigDecimal


class SavingsEditActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_savings_edit)

        //For when back gesture or button is triggered will return to Main[financial obj]Activity
        val callback = onBackPressedDispatcher.addCallback(this) {
            setResult(RESULT_OK)
            finish()
        }

        //Retrieve all views from savings edit layout
        val savingsName = findViewById<EditText>(R.id.etSavingsEditName)
        val savingsDescription = findViewById<EditText>(R.id.etSavingsEditDescription)
        val savingsGoalAmount = findViewById<EditText>(R.id.etSavingsEditGoalAmount)
        val savingsStatus = findViewById<TextView>(R.id.tvSavingsEditStatus)
        val savingsId = findViewById<TextView>(R.id.tvSavingsEditId)
        val savingDateCreated = findViewById<TextView>(R.id.tvSavingsEditDateCreated)


        //Retrieve Data from bundle
        val bundle : Bundle? = intent.extras
        val name = bundle!!.getString("id")

        val financialObject = FileManager.loadFinancialObject(
            applicationContext.filesDir.absolutePath,
            Constants.SAVINGS_FILENAME
        )
        val financialSavings = financialObject.savingsObjects.get(name)
        savingsName.setText(financialSavings?.name)
        savingsDescription.setText(financialSavings?.description)
        savingsGoalAmount.setText(BigDecimalTools.prepareForPrintNC(financialSavings?.amount))
        savingsStatus.text = financialSavings?.status
        savingsId.text = financialSavings?.financialObjectID
        savingDateCreated.text = CalendarTools.getCalendarFormatDDMMYYYY(
            financialSavings?.dateCreated
        )

        //Save changes button
        val bSavingsEditSaveChanges = findViewById<Button>(R.id.bSavingsEditSaveChanges)
        bSavingsEditSaveChanges.setOnClickListener {
            financialSavings?.name = savingsName.text.toString()
            financialSavings?.description = savingsDescription.text.toString()
            if(StringTools.isNumeric(savingsGoalAmount.text.toString())){
                financialSavings?.amount = BigDecimal(savingsGoalAmount.text.toString())
            }
            FileManager.saveFinancialObject(
                financialObject,
                applicationContext.filesDir.absolutePath,
                Constants.SAVINGS_FILENAME
            )
            val intent = Intent()
            intent.putExtra("id", financialSavings?.financialObjectID)
            setResult(RESULT_OK, intent)
            finish()
        }

        //Alert Dialog for Delete Confimation
        val builder = AlertDialog.Builder(this)
        val savingsNameString = financialSavings?.name
        builder.setTitle("Delete $savingsNameString ?")
        builder.setMessage(
            "This action cannot be undone and $savingsNameString data will be lost forever"
        )

        //Dismisses Alert Dialog if user chooses not to delete
        builder.setPositiveButton("NO") { dialog, which ->
            dialog.dismiss()
        }

        //Deletion Process Begins when alert dialog for delete is confirmed
        builder.setNegativeButton("YES"){dialog, which->
            financialObject.deleteSavingsObject(financialSavings?.financialObjectID)
            FileManager.saveFinancialObject(
                financialObject,
                applicationContext.filesDir.absolutePath,
                Constants.SAVINGS_FILENAME
            )
            Toast.makeText(
                this,
                "$savingsNameString Successfully Deleted",
                Toast.LENGTH_SHORT
            ).show()

            setResult(RESULT_CANCELED)
            finish()
        }


        val ibSavingsEditDelete = findViewById<ImageButton>(R.id.ibEditSavingsDelete)
        ibSavingsEditDelete.setOnClickListener {
            builder.show()
        }


    }
}