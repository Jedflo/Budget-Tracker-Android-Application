package com.example.budgettracker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.math.BigDecimal

class WalletAddActivity : AppCompatActivity() {
    private lateinit var sqLiteHelper: SQLiteHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallet_add)
        sqLiteHelper = SQLiteHelper(this)

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
            val newWallet = FinancialObjectModel(
                type = Const.FO_TYPE_WALLET,
                name = newWalletName,
                description = newWalletDescription
            )
            val newWalletKey = newWallet.id
            sqLiteHelper.insertFinancialObject(newWallet)

            //send toast to user which confirms that the file has been saved
            Toast.makeText(
                this,
                "$newWalletName, $newWalletDescription, $newWalletKey",
                Toast.LENGTH_SHORT
            ).show()

            val intent = Intent(applicationContext, MainWalletActivity::class.java)
            intent.putExtra(Const.INTENT_KEY_WALLET_ID, newWalletKey)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            setResult(RESULT_OK)
            finish()

        }

    }
}