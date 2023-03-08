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

class WalletEditActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallet_edit)

        //For when back gesture or button is triggered will return to Main[financial obj]Activity
        val callback = onBackPressedDispatcher.addCallback(this) {
            setResult(RESULT_OK)
            finish()
        }

        //Retrieve all views from activity wallet edit
        val displayWalletName = findViewById<EditText>(R.id.etWalletEditName)
        val displayWalletDescription = findViewById<EditText>(R.id.etWalletEditDescription)
        val displayWalletId = findViewById<TextView>(R.id.tvWalletId)
        val displayWalletDateCreated = findViewById<TextView>(R.id.tvWalletDateCreated)

        //Retrieve Data from bundle
        val bundle: Bundle? = intent.extras
        val walletID = bundle!!.getString("wallet id")
        //Retrieve relevant info from save file
        val financialObject = FileManager.loadFinancialObject(
            applicationContext.filesDir.absolutePath,
            Constants.SAVINGS_FILENAME
        )
        val wallet = financialObject.childFinancialObjects.get(walletID)
        displayWalletName.setText(wallet?.name)
        displayWalletDescription.setText(wallet?.description)
        displayWalletId.text = wallet?.financialObjectID
        displayWalletDateCreated.text = CalendarTools.getCalendarFormatDDMMYYYY(wallet?.dateCreated)

        //Save changes button
        val bWalletEditSaveChanges = findViewById<Button>(R.id.bWalletEditSaveChanges)
        bWalletEditSaveChanges.setOnClickListener {
            wallet?.name = displayWalletName.text.toString()
            wallet?.description = displayWalletDescription.text.toString()
            FileManager.saveFinancialObject(
                financialObject,
                applicationContext.filesDir.absolutePath,
                Constants.SAVINGS_FILENAME
            )
            val nextIntent = Intent()
            nextIntent.putExtra("walletId", wallet?.financialObjectID)
            setResult(RESULT_OK,nextIntent)
            finish()

        }

        //Alert Dialog for Delete Confimation
        val builder = AlertDialog.Builder(this)
        val walletNameString = wallet?.name
        builder.setTitle("Delete $walletNameString ?")
        builder.setMessage(
            "This action cannot be undone and $walletNameString data will be lost forever"
        )

        //Dismisses Alert Dialog if user chooses not to delete
        builder.setPositiveButton("NO") { dialog, which ->
            dialog.dismiss()
        }

        //Deletion Process Begins when alert dialog for delete is confirmed
        builder.setNegativeButton("YES"){dialog, which->
            financialObject.deleteChildObject(wallet?.financialObjectID)
            FileManager.saveFinancialObject(
                financialObject,
                applicationContext.filesDir.absolutePath,
                Constants.SAVINGS_FILENAME
            )
            Toast.makeText(
                this,
                "$walletNameString Successfully Deleted",
                Toast.LENGTH_SHORT
            ).show()

            setResult(RESULT_CANCELED)
            finish()
        }

        val ibWalletEditDelete = findViewById<ImageButton>(R.id.ibWalletEditDelete)
        ibWalletEditDelete.setOnClickListener {
            builder.show()
        }



    }


}