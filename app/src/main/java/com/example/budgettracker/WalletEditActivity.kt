package com.example.budgettracker

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class WalletEditActivity : AppCompatActivity() {
    private lateinit var displayWalletName: EditText
    private lateinit var displayWalletDescription: EditText
    private lateinit var displayWalletId: TextView
    private lateinit var displayWalletDateCreated: TextView
    private lateinit var bWalletEditSaveChanges: Button
    private lateinit var ibWalletEditDelete: ImageButton
    private lateinit var sqLiteHelper: SQLiteHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallet_edit)

        //Retrieve all views from activity wallet edit
        initViews()
        sqLiteHelper = SQLiteHelper(this)

        //For when back gesture or button is triggered will return to Main[financial obj]Activity
        val callback = onBackPressedDispatcher.addCallback(this) {
            setResult(RESULT_OK)
            finish()
        }

        //Retrieve Data from bundle
        val bundle: Bundle? = intent.extras
        val walletID = bundle!!.getString(Const.INTENT_KEY_WALLET_ID)

        //Check if retrieved wallet ID from bundle is null or empty. if it is throw Alert.
        if (walletID.isNullOrEmpty()){
            val builder = AlertDialog.Builder(this)
            builder.setPositiveButton("OK"){dialog, which ->
                dialog.dismiss()
                setResult(RESULT_OK)
                finish()
            }
            builder.setTitle("Wallet ID could not be found.")
            builder.show()
        }

        //Retrieve relevant info from database
        val wallet = sqLiteHelper.getFinancialObject(walletID!!)

        //Fill out necessary edit texts
        displayWalletName.setText(wallet?.name)
        displayWalletDescription.setText(wallet?.description)
        displayWalletId.text = wallet?.id
        displayWalletDateCreated.text = CalendarTools.getCalendarFormatForDatabase(wallet?.dateCreated)
        //TODO Add Level, might be dropdown or checkbox.

        //Save changes button
        bWalletEditSaveChanges.setOnClickListener {
            wallet?.name = displayWalletName.text.toString()
            wallet?.description = displayWalletDescription.text.toString()
            sqLiteHelper.updateFinancialObject(wallet!!)
            val nextIntent = Intent()
            nextIntent.putExtra(Const.INTENT_KEY_WALLET_ID, wallet?.id)
            setResult(RESULT_OK,nextIntent)
            finish()

        }

        //Alert Dialog for Delete Confimation
        val builder = AlertDialog.Builder(this)
        val walletNameString = wallet?.name
        builder.setTitle("Delete $walletNameString ?")
        builder.setMessage(
            "This action cannot be undone and all related data to $walletNameString will be lost forever"
        )

        //Dismisses Alert Dialog if user chooses not to delete
        builder.setPositiveButton("NO") { dialog, which ->
            dialog.dismiss()
        }

        //Deletion Process Begins when alert dialog for delete is confirmed
        builder.setNegativeButton("YES"){dialog, which->
            sqLiteHelper.deleteFinancialObject(wallet!!)
            Toast.makeText(
                this,
                "$walletNameString Successfully Deleted",
                Toast.LENGTH_SHORT
            ).show()

            setResult(RESULT_CANCELED)
            finish()
        }

        ibWalletEditDelete.setOnClickListener {
            builder.show()
        }



    }

    private fun initViews(){
        displayWalletName = findViewById(R.id.etWalletEditName)
        displayWalletDescription = findViewById(R.id.etWalletEditDescription)
        displayWalletId = findViewById(R.id.tvWalletId)
        displayWalletDateCreated = findViewById(R.id.tvWalletDateCreated)
        bWalletEditSaveChanges = findViewById(R.id.bWalletEditSaveChanges)
        ibWalletEditDelete = findViewById(R.id.ibWalletEditDelete)
    }


}