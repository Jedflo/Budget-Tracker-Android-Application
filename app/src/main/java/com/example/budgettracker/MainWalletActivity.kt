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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.math.BigDecimal

class MainWalletActivity : AppCompatActivity() {
    private lateinit var walletTransactionsList: ArrayList<FinancialObjectTransactionModel>
    private lateinit var walletTransactionsRecyclerView: RecyclerView
    private lateinit var walletName: TextView
    private lateinit var walletDescription: TextView
    private lateinit var walletAmount: TextView
    private lateinit var sqLiteHelper: SQLiteHelper
    private lateinit var bAddWalletTransaction: Button
    private lateinit var bSubWalletTransaction: Button
    private lateinit var bTranferFromWallet: Button

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
        initViews()
        sqLiteHelper = SQLiteHelper(this)

        //Retrieve information from bundle sent from Wallet Fragment
        val bundle: Bundle? = intent.extras
        val walletId = bundle!!.getString(Const.INTENT_KEY_WALLET_ID)

        //retrieve wallet object using wallet Id to retrieve wallet details..
        val wallet = sqLiteHelper.getFinancialObject(walletId!!)



        //Set wallet details in activity.
        walletName.text = wallet?.name
        walletDescription.text = wallet?.description
        walletAmount.text = prepareDoubleForPrint(wallet?.valueAmount!!)

        title = wallet?.name

        //Populate the FT recyclerview.
        initializeWalletTransactionData(walletId)//TODO use calendar for date in FinancialObjectTransactionModel
        var financialTransactionRecyclerView= findViewById<RecyclerView>(R.id.rvTransactionsWallet)
        financialTransactionRecyclerView.layoutManager = LinearLayoutManager(this)
        financialTransactionRecyclerView.setHasFixedSize(true)
        //Sort transactions by date. newest first, oldest last.
        walletTransactionsList.sortWith((
                Comparator.comparing(
                    FinancialObjectTransactionModel::transactionDate
                )).reversed())
        var walletTransactionAdapter = FinancialTransactionWalletAdapter(walletTransactionsList)
        financialTransactionRecyclerView.adapter = walletTransactionAdapter

        walletTransactionAdapter.setOnItemClickListener(object : FinancialTransactionWalletAdapter.onItemClickListener{
            override fun onItemClick(position: Int) {
                val clickedItem = walletTransactionsList[position]
                clickedItem.id
                Toast.makeText(
                    this@MainWalletActivity,
                    "Transaction ID:" + clickedItem.id,
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        //Get add and subtract buttons from layout
        //Check if wallet balance is >0, if it is, enable subtract button, else leave disabled.
        val walletTotal = wallet?.valueAmount
        val isButtonsEnabled = walletTotal!! > 0
        bSubWalletTransaction.isEnabled = isButtonsEnabled
        bTranferFromWallet.isEnabled = isButtonsEnabled

        val intent = Intent(this, WalletCreateTransactionActivity::class.java)

        //Button for adding to wallet
        bAddWalletTransaction.setOnClickListener {
            intent.putExtra(Const.INTENT_KEY_TRANSACTION_TYPE, Const.INTENT_VALUE_ADD_TRANSACTION)
            intent.putExtra(Const.INTENT_KEY_WALLET_ID, walletId)
            activityResultLauncher.launch(intent)
        }

        //Button for subtracting from wallet.
        bSubWalletTransaction.setOnClickListener {
            intent.putExtra(Const.INTENT_KEY_TRANSACTION_TYPE, Const.INTENT_VALUE_SUB_TRANSACTION)
            intent.putExtra(Const.INTENT_KEY_WALLET_ID, walletId)
            activityResultLauncher.launch(intent)
        }

        //Button for editing wallet
        val bEditWalletDetails = findViewById<Button>(R.id.bEditWallet)
        bEditWalletDetails.setOnClickListener {
            val intent = Intent(this, WalletEditActivity::class.java)
            intent.putExtra("wallet id", walletId)
            activityResultLauncher.launch(intent)
        }

//        //Button for transfer
//
//        bTranferFromWallet.setOnClickListener {
//            val intent = Intent(this, GeneralTransferActivity::class.java)
//            intent.putExtra("wallet id", walletId)
//            intent.putExtra(Constants.INTENT_KEY_RQST_FR, Constants.INTENT_VAL_RQST_FR_WALLET)
//            activityResultLauncher.launch(intent)
//        }

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

    private fun initViews(){
        walletName = findViewById(R.id.tvMainWalletName)
        walletDescription = findViewById(R.id.tvMainWalletDescription)
        walletAmount = findViewById(R.id.tvMainWalletTotalAmount)
        bAddWalletTransaction = findViewById(R.id.bAddTransactionWallet)
        bSubWalletTransaction = findViewById(R.id.bSubTransactionWallet)
        bTranferFromWallet = findViewById(R.id.bTransferTransactionWallet)
    }

    private fun initializeWalletTransactionData(walletId: String){
        walletTransactionsList = sqLiteHelper.getTransactionsOnFinancialObject(walletId)
    }

}