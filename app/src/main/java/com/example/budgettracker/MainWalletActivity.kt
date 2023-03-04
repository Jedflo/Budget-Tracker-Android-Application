package com.example.budgettracker

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.math.BigDecimal

class MainWalletActivity : AppCompatActivity() {
    private lateinit var walletTransactionsList: ArrayList<FinancialTransactionWalletModel>
    private lateinit var walletTransactionsRecyclerView: RecyclerView

    lateinit var mainWalletAct: Activity

    var activityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){ result ->
        if (result.resultCode == RESULT_OK){
            finish()
            startActivity(intent)
        }
        else{
            setResult(RESULT_OK)
            val intent = Intent(applicationContext, MainActivity::class.java)
            intent.putExtra(Constants.INTENT_KEY_NAVIGATE_TO, Constants.NAVIGATE_TO_WALLET)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_wallet)
        /* So create transaction can close this activity and start a new instance of this act to
        update the data.
        */
        mainWalletAct = this

        //Retrieve all views from layout.
        val walletName: TextView = findViewById(R.id.tvMainWalletName)
        val walletDescription: TextView = findViewById(R.id.tvMainWalletDescription)
        val walletAmount: TextView = findViewById(R.id.tvMainWalletTotalAmount)

        //Retrieve information from bundle sent from Wallet Fragment
        val bundle: Bundle? = intent.extras
        val walletId = bundle!!.getString("walletId")

        //retrieve wallet object using wallet Id to retrieve wallet details..
        val wallet = FileManager.loadFinancialObject(
            applicationContext.filesDir.absolutePath,
            Constants.SAVINGS_FILENAME
        ).childFinancialObjects.get(walletId)

        //Set wallet details in activity.
        walletName.text = wallet?.name
        walletDescription.text = wallet?.description
        walletAmount.text = BigDecimalTools.prepareForPrint(wallet?.financialTransactionsTotal)

        setTitle(wallet?.name)

        //Populate the FT recyclerview.
        initializeWalletTransactionData(walletId)
        var financialTransactionRecyclerView= findViewById<RecyclerView>(R.id.rvTransactionsWallet)
        financialTransactionRecyclerView.layoutManager = LinearLayoutManager(this)
        financialTransactionRecyclerView.setHasFixedSize(true)
        walletTransactionsList.sortWith((
                Comparator.comparing(
                    FinancialTransactionWalletModel::ftwTransactionDate
                )).reversed())
        var walletTransactionAdapter = FinancialTransactionWalletAdapter(walletTransactionsList)
        financialTransactionRecyclerView.adapter = walletTransactionAdapter

        walletTransactionAdapter.setOnItemClickListener(object : FinancialTransactionWalletAdapter.onItemClickListener{
            override fun onItemClick(position: Int) {
                val clickedItem = walletTransactionsList.get(position)
                clickedItem.ftwId
                Toast.makeText(
                    this@MainWalletActivity,
                    "Transaction ID:" + clickedItem.ftwId,
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        //Get add and subtract buttons from layout
        val bAddWalletTransaction = findViewById<Button>(R.id.bAddTransactionWallet)
        val bSubWalletTransaction = findViewById<Button>(R.id.bSubTransactionWallet)
        //Check if wallet balance is >0, if it is, enable subtract button, else leave disabled.
        val walletTotal = wallet?.financialTransactionsTotal
        bSubWalletTransaction.isEnabled = walletTotal?.compareTo(BigDecimal.ZERO)!! >0

        val intent = Intent(this, WalletCreateTransactionActivity::class.java)

        //Button for adding to wallet
        bAddWalletTransaction.setOnClickListener {
            intent.putExtra("transaction type", "add")
            intent.putExtra("wallet id", walletId)
            activityResultLauncher.launch(intent)
        }

        //Button for subtracting from wallet.
        bSubWalletTransaction.setOnClickListener {
            intent.putExtra("transaction type", "sub")
            intent.putExtra("wallet id", walletId)
            activityResultLauncher.launch(intent)
        }

        //Button for editing wallet
        val bEditWalletDetails = findViewById<Button>(R.id.bEditWallet)
        bEditWalletDetails.setOnClickListener {
            val intent = Intent(this, WalletEditActivity::class.java)
            intent.putExtra("wallet id", walletId)
            activityResultLauncher.launch(intent)
        }

        //Button for transfer
        val bTranferFromWallet = findViewById<Button>(R.id.bTransferTransactionWallet)
        bTranferFromWallet.setOnClickListener {
            val intent = Intent(this, GeneralTransferActivity::class.java)
            intent.putExtra("wallet id", walletId)
            intent.putExtra(Constants.INTENT_KEY_RQST_FR, Constants.INTENT_VAL_RQST_FR_WALLET)
            activityResultLauncher.launch(intent)
        }

        //Back Button
        val bBackToWalletHome = findViewById<Button>(R.id.bBackWallet)
        bBackToWalletHome.setOnClickListener {
            val intent = Intent(applicationContext, MainActivity::class.java)
            intent.putExtra(Constants.INTENT_KEY_NAVIGATE_TO, Constants.NAVIGATE_TO_WALLET)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            setResult(RESULT_OK)
            startActivity(intent)
            finish()
        }
    }

    private fun initializeWalletTransactionData(walletId: String?){
        //initialize wallet transaction list
        walletTransactionsList = arrayListOf<FinancialTransactionWalletModel>()
        val fo: FinancialObject
        fo = FileManager.loadFinancialObject(
            applicationContext.filesDir.absolutePath,
            Constants.SAVINGS_FILENAME
        )
        val walletFinancialTransactions = fo.childFinancialObjects.get(walletId)?.transactions?:
        return

        for(financialTransaction in walletFinancialTransactions.values){
            val walletFT = FinancialTransactionWalletModel(
                financialTransaction.financialTransactionID,
                financialTransaction.name,
                financialTransaction.amount,
                financialTransaction.dateCreated
            )
            walletTransactionsList.add(walletFT)
        }


    }

}