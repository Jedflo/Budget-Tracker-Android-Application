package com.example.budgettracker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import java.math.BigDecimal

class WalletAddActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallet_add)

        //Find views where we will get the data to create a wallet first
        val bCreateWallet = findViewById<Button>(R.id.bWalletAddCreateWallet)
        val etWalletName = findViewById<EditText>(R.id.etWalletAddWalletName)
        val etWalletDescription = findViewById<EditText>(R.id.etWalletAddWalletDescription)

        bCreateWallet.setOnClickListener {
            //On button click, get the name and description of wallet to be created.
            val newWalletName = etWalletName.text.toString()
            val newWalletDescription = etWalletDescription.text.toString()

            //Validations
            val builder = AlertDialog.Builder(this)

            builder.setPositiveButton("OK") { dialog, which ->
                dialog.dismiss()
            }

            if (StringTools.isNullOrEmpty(newWalletName)){
                builder.setTitle("Wallet Name must not be blank")
                builder.show()
                return@setOnClickListener
            }

            //create wallet using collected data.
            val newWallet = FinancialObject(newWalletName,newWalletDescription, BigDecimal.ZERO)
            val newWalletKey = newWallet.financialObjectID

            //Load Financial Object
            val mainFinancialObject = FileManager.loadFinancialObject(
                applicationContext.filesDir.absolutePath,
                Constants.SAVINGS_FILENAME
            )

            //Add new wallet to Financial Object
            mainFinancialObject.childFinancialObjects.put(newWalletKey, newWallet)

            FileManager.saveFinancialObject(
                mainFinancialObject,
                applicationContext.filesDir.absolutePath,
                Constants.SAVINGS_FILENAME
            )

            //send toast to user which confirms that the file has been saved
            Toast.makeText(
                this,
                "$newWalletName, $newWalletDescription, $newWalletKey",
                Toast.LENGTH_SHORT
            ).show()

            val intent = Intent(applicationContext, MainWalletActivity::class.java)
            intent.putExtra("walletId", newWalletKey)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            setResult(RESULT_OK)
            finish()

        }

    }
}