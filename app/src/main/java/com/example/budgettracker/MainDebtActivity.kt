package com.example.budgettracker

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.math.BigDecimal

class MainDebtActivity : AppCompatActivity() {
    private lateinit var debtTransactionsList: ArrayList<FinancialTransactionDebtModel>
    private lateinit var debtTransactionsRecyclerView: RecyclerView

    lateinit var mainDebtAct: Activity

    var activityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){ result ->
        if(result.resultCode == RESULT_OK){
            finish()
            startActivity(intent)
        }
        else{
            setResult(RESULT_OK)
            val intent = Intent(applicationContext, MainActivity::class.java)
            intent.putExtra(Constants.INTENT_KEY_NAVIGATE_TO, Constants.NAVIGATE_TO_DEBT)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_debt)
        //so create transaction can close this activity and start a new instance of this act to
        //update the data.
        mainDebtAct = this

        //Retrieve all views from layout.
        val debtName: TextView = findViewById(R.id.tvMainDebtName)
        val debtPaymentsTotal: TextView = findViewById(R.id.tvMainDebtTotalAmount)
        val debtAmount: TextView = findViewById(R.id.tvMainDebtDebtAmount)
        val debtDescription: EditText = findViewById(R.id.etMainDebtDescription)
        val debtCompletedMark: ImageView = findViewById(R.id.ivDebtCompeltedMark)

        //Retrieve data from bundle
        val bundle : Bundle? = intent.extras
        val debtId = bundle!!.getString("debtId")

        //retrieve relevant info for savings amount text view and description edit text
        val financialDebt = FileManager.loadFinancialObject(
            applicationContext.filesDir.absolutePath,
            Constants.SAVINGS_FILENAME
        ).debtObjects.get(debtId)

        //set name and goal amount text views
        debtName.text = financialDebt?.name
        debtAmount.text = BigDecimalTools.prepareForPrint(financialDebt?.amount)
        debtPaymentsTotal.text = BigDecimalTools.prepareForPrint(
            financialDebt?.financialTransactionsTotal
        )
        debtDescription.setText(financialDebt?.description)

        //Set title of activity
        setTitle(financialDebt?.name)

        //Check if debt payments >= debt amount, if so make check mark visible.
        if(financialDebt?.amount?.compareTo(financialDebt?.financialTransactionsTotal)!! <=0){
            debtCompletedMark.visibility = View.VISIBLE
        }

        //Use debt id to retrieve related FTs to populate debt financial transaction recycler view.
        initializeDebtTransactionData(debtId)
        var financialTransactionsRecyclerView = findViewById<RecyclerView>(R.id.rvTransactionMd)
        financialTransactionsRecyclerView.layoutManager = LinearLayoutManager(this)
        financialTransactionsRecyclerView.setHasFixedSize(true)
        debtTransactionsList.sortWith((
                Comparator.comparing(
                    FinancialTransactionDebtModel::ftdTransacDate
                )).reversed())
        var financialDebtAdapter = FinancialTransactionDebtAdapter(debtTransactionsList)
        financialTransactionsRecyclerView.adapter = financialDebtAdapter

        financialDebtAdapter.setOnItemClickListener(
            object : FinancialTransactionDebtAdapter.onItemClickListener{
            override fun onItemClick(position: Int) {
                val clickedItem = debtTransactionsList.get(position)
                clickedItem.ftdId
                Toast.makeText(
                    this@MainDebtActivity,
                    "Transaction ID: " + clickedItem.ftdId,
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        val bAddDebtTransaction = findViewById<Button>(R.id.bAddTransactionMd)
        val bSubDebtTransaction = findViewById<Button>(R.id.bSubTransactionMd)
        val bTranferFromSavings = findViewById<Button>(R.id.bTransferTransactionMd)
        val intent = Intent(this, DebtCreateTransactionActivity::class.java)

        val debtPayTotal = financialDebt.financialTransactionsTotal
        val isButtonsEnabled = debtPayTotal?.compareTo(BigDecimal.ZERO)!! >0
        bSubDebtTransaction.isEnabled = isButtonsEnabled
        bTranferFromSavings.isEnabled = isButtonsEnabled

        //Button for adding to debt
        bAddDebtTransaction.setOnClickListener {
            intent.putExtra("transaction type", "add")
            intent.putExtra("debtId", debtId)
            activityResultLauncher.launch(intent)
        }

        //Button for subtracting from debt
        bSubDebtTransaction.setOnClickListener {
            intent.putExtra("transaction type", "sub")
            intent.putExtra("debtId", debtId)
            activityResultLauncher.launch(intent)
        }

        val bEditDebtDetails = findViewById<Button>(R.id.bEditMd)
        bEditDebtDetails.setOnClickListener {
            val intent = Intent(this, DebtEditActivity::class.java)
            intent.putExtra("debtId", debtId)
            activityResultLauncher.launch(intent)
        }

        //Button for transfer
        bTranferFromSavings.setOnClickListener {
            val intent = Intent(this, GeneralTransferActivity::class.java)
            intent.putExtra("debtId", debtId)
            intent.putExtra(Constants.INTENT_KEY_RQST_FR, Constants.INTENT_VAL_RQST_FR_DEBT)
            activityResultLauncher.launch(intent)
        }

        //Back Button
        val bBackToSavingsHome = findViewById<Button>(R.id.bBackDebt)
        bBackToSavingsHome.setOnClickListener {
            val intent = Intent(applicationContext, MainActivity::class.java)
            intent.putExtra(Constants.INTENT_KEY_NAVIGATE_TO, Constants.NAVIGATE_TO_DEBT)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            setResult(RESULT_OK)
            startActivity(intent)
            finish()
        }


    }

    private fun initializeDebtTransactionData(debtId:String?){
        //initialize savings transaction list.
        debtTransactionsList = arrayListOf<FinancialTransactionDebtModel>()
        val fo: FinancialObject
        fo = FileManager.loadFinancialObject(
            applicationContext.filesDir.absolutePath,
            Constants.SAVINGS_FILENAME
        )
        val debtFinancialTransactions = fo.debtObjects.get(debtId)?.transactions
            ?: return

        for(financialTransaction in debtFinancialTransactions.values){
            val debtFT = FinancialTransactionDebtModel(
                financialTransaction.financialTransactionID,
                financialTransaction.name,
                financialTransaction.amount,
                financialTransaction.dateCreated
            )
            debtTransactionsList.add(debtFT)
        }
    }

}