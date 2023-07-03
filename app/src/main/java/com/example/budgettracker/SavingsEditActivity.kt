package com.example.budgettracker

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.math.BigDecimal


class SavingsEditActivity : AppCompatActivity() {
    private lateinit var savingsName: EditText
    private lateinit var savingsDescription: EditText
    private lateinit var savingsGoalAmount: EditText
    private lateinit var savingsStatus: TextView
    private lateinit var savingsId: TextView
    private lateinit var savingDateCreated: TextView
    private lateinit var sqLiteHelper: SQLiteHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_savings_edit)

        //Retrieve all views from savings edit layout
        initViews()
        sqLiteHelper = SQLiteHelper(this)

        //For when back gesture or button is triggered will return to Main[financial obj]Activity
        val callback = onBackPressedDispatcher.addCallback(this) {
            setResult(RESULT_OK)
            finish()
        }

        //Retrieve Data from bundle
        val bundle : Bundle? = intent.extras
        val savingsId = bundle!!.getString(Const.INTENT_KEY_SAVINGS_ID)

        //Check if retrieved wallet ID from bundle is null or empty. if it is throw Alert.
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

        //Retrieve financial object from database.
        val financialSavings  = sqLiteHelper.getFinancialObject(savingsId!!)

        //From retrieved financial object, fill out necessary edit texts.
        savingsName.setText(financialSavings?.name)
        savingsDescription.setText(financialSavings?.description)
        savingsGoalAmount.setText(prepareDoubleForPrint(financialSavings?.targetAmount!!))
        //savingsStatus.text = financialSavings?.status
        this.savingsId.text = financialSavings?.id
        savingDateCreated.text = CalendarTools.getCalendarFormatDDMMYYYY(
            financialSavings?.dateCreated
        )

        //Save changes button
        val bSavingsEditSaveChanges = findViewById<Button>(R.id.bSavingsEditSaveChanges)
        bSavingsEditSaveChanges.setOnClickListener {
            financialSavings?.name = savingsName.text.toString()
            financialSavings?.description = savingsDescription.text.toString()
            if(StringTools.isNumeric(savingsGoalAmount.text.toString())){
                financialSavings?.targetAmount = savingsGoalAmount.text.toString()
                    .replace(",","").toDouble()
            }
            sqLiteHelper.updateFinancialObject(financialSavings)
            val intent = Intent()
            intent.putExtra(Const.INTENT_KEY_SAVINGS_ID, financialSavings?.id)
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
            sqLiteHelper.deleteFinancialObject(financialSavings)
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

    private fun initViews() {
        savingsName = findViewById(R.id.etSavingsEditName)
        savingsDescription = findViewById(R.id.etSavingsEditDescription)
        savingsGoalAmount = findViewById(R.id.etSavingsEditGoalAmount)
        savingsGoalAmount.addTextChangedListener(NumberTextWatcher(savingsGoalAmount))
        savingsStatus = findViewById(R.id.tvSavingsEditStatus)
        savingsId = findViewById(R.id.tvSavingsEditId)
        savingDateCreated = findViewById(R.id.tvSavingsEditDateCreated)
    }
}