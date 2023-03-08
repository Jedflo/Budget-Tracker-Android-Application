package com.example.budgettracker

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import java.math.BigDecimal

class DebtEditActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_debt_edit)

        //For when back gesture or button is triggered will return to Main[financial obj]Activity
        val callback = onBackPressedDispatcher.addCallback(this) {
            setResult(RESULT_OK)
            finish()
        }

        //Retrieve all views from savings edit layout
        val debtName = findViewById<EditText>(R.id.etDebtEditName)
        val debtDescription = findViewById<EditText>(R.id.etDebtEditDescription)
        val debtAmount = findViewById<EditText>(R.id.etDebtEditAmount)
        val debtStatus = findViewById<TextView>(R.id.tvDebtStatus)
        val debtId = findViewById<TextView>(R.id.tvDebtId)
        val debtDateCreated = findViewById<TextView>(R.id.tvDebtDateCreated)

        //Retrieve Data from bundle
        val bundle : Bundle? = intent.extras
        val debtIdNo = bundle!!.getString("debtId")

        val financialObject = FileManager.loadFinancialObject(
            applicationContext.filesDir.absolutePath,
            Constants.SAVINGS_FILENAME
        )

        val financialDebt = financialObject.debtObjects.get(debtIdNo)
        debtName.setText(financialDebt?.name)
        debtDescription.setText(financialDebt?.description)
        debtAmount.setText(BigDecimalTools.prepareForPrintNC(financialDebt?.amount))
        debtStatus.text = financialDebt?.status
        debtId.text = financialDebt?.financialObjectID
        debtDateCreated.text = CalendarTools.getCalendarFormatDDMMYYYY(financialDebt?.dateCreated)

        //Save changes button
        val bDebtEditSaveChanges = findViewById<Button>(R.id.bDebtEditSaveChanges)
        bDebtEditSaveChanges.setOnClickListener {
            financialDebt?.name = debtName.text.toString()
            financialDebt?.description = debtDescription.text.toString()
            if(StringTools.isNumeric(debtAmount.text.toString())){
                financialDebt?.amount = BigDecimal(debtAmount.text.toString())
            }
            FileManager.saveFinancialObject(
                financialObject,
                applicationContext.filesDir.absolutePath,
                Constants.SAVINGS_FILENAME
            )
            val intent = Intent()
            intent.putExtra("debtId", financialDebt?.financialObjectID)
            setResult(RESULT_OK,intent)
            finish()

        }


        //Alert Dialog for Delete Confimation
        val builder = AlertDialog.Builder(this)
        val debtNameString = financialDebt?.name
        builder.setTitle("Delete $debtNameString ?")
        builder.setMessage(
            "This action cannot be undone and $debtNameString data will be lost forever"
        )

        //Dismisses Alert Dialog if user chooses not to delete
        builder.setPositiveButton("NO") { dialog, which ->
            dialog.dismiss()
        }

        //Deletion Process Begins when alert dialog for delete is confirmed
        builder.setNegativeButton("YES"){dialog, which->
            financialObject.deleteDebtObject(financialDebt?.financialObjectID)
            FileManager.saveFinancialObject(
                financialObject,
                applicationContext.filesDir.absolutePath,
                Constants.SAVINGS_FILENAME
            )
            Toast.makeText(
                this,
                "$debtNameString Successfully Deleted",
                Toast.LENGTH_SHORT
            ).show()

            setResult(RESULT_CANCELED)
            finish()
        }

        val ibWalletEditDelete = findViewById<ImageButton>(R.id.ibDebtEditDelete)
        ibWalletEditDelete.setOnClickListener {
            builder.show()
        }

    }

}