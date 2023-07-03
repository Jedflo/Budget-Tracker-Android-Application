package com.example.budgettracker

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.math.BigDecimal


class MainSavingsActivity : AppCompatActivity() {
    private lateinit var savingsTransactionList: ArrayList<FinancialObjectTransactionModel>
    private lateinit var savingsTransactionsRecyclerView: RecyclerView
    private lateinit var sqLiteHelper: SQLiteHelper
    private lateinit var savingsName: TextView
    private lateinit var savingsGoalAmount: TextView
    private lateinit var savingsAmount: TextView
    private lateinit var savingsDescription: EditText
    private lateinit var savingsCompletedMark: ImageView
    private lateinit var bAddSavingsTransaction: Button
    private lateinit var bSubSavingsTransaction: Button
    private lateinit var bTranferFromSavings: Button


    lateinit var mainSavingsAct: Activity

    private val activityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){
        if (it.resultCode == Activity.RESULT_OK){
            finish()
            startActivity(intent)
        }
        else{
            setResult(RESULT_OK)
            val intent = Intent(applicationContext, MainActivity::class.java)
            intent.putExtra(Constants.INTENT_KEY_NAVIGATE_TO, Constants.NAVIGATE_TO_SAVINGS)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_savings)
        //so create transaction can close this activity and start a new instance of this act to
        //update the data.
        mainSavingsAct = this

        //Retrieve all views from layout.
        initViews()
        sqLiteHelper = SQLiteHelper(this)

        //Retrieve data from bundle
        val bundle : Bundle? = intent.extras
        val id = bundle!!.getString(Const.INTENT_KEY_SAVINGS_ID)

        //TODO create alert dialog error for checking id from bundle

        //retrieve relevant info for savings amount text view and description edit text
        val financialSavings = sqLiteHelper.getFinancialObject(id!!)


        //set savings details in activity
        savingsName.text = financialSavings?.name
        savingsGoalAmount.text = prepareDoubleForPrint(financialSavings!!.targetAmount)
        savingsAmount.text = prepareDoubleForPrint(financialSavings!!.valueAmount)
        savingsDescription.setText(financialSavings?.description)

        //Set title of activity
        title = financialSavings?.name

        //Check if savings has reached goal, if so make check mark visible.
        if(financialSavings!!.targetAmount <= financialSavings!!.valueAmount){
            savingsCompletedMark.visibility = View.VISIBLE
        }

        //Use savings name to retrieve related FTs to populate savings financial transaction recycler view.
        initializeSavingsTransactionData(id)
        var financialTransactionsRecyclerView =findViewById<RecyclerView>(R.id.rvTransactionsMs)
        financialTransactionsRecyclerView.layoutManager = LinearLayoutManager(this)
        financialTransactionsRecyclerView.setHasFixedSize(true)
        savingsTransactionList.sortWith((
                Comparator.comparing(
                    FinancialObjectTransactionModel::transactionDate
                )).reversed())
        var financialSavingsAdapter = FinancialTransactionSavingsAdapter(savingsTransactionList)
        financialTransactionsRecyclerView.adapter = financialSavingsAdapter

        financialSavingsAdapter.setOnItemClickListener(object : FinancialTransactionSavingsAdapter.onItemClickListener{
            override fun onItemClick(position: Int) {
                val clickedItem = savingsTransactionList[position]
                clickedItem.id
                Toast.makeText(
                    this@MainSavingsActivity,
                    "Transaction ID: " + clickedItem.id,
                    Toast.LENGTH_SHORT
                ).show()
            }

        })


        val intent = Intent(this, SavingsCreateTransactionActivity::class.java)

        val savingsTotal = financialSavings.valueAmount

        //Check if savings amount is greater than 0. If it is, enable subtract and transfer
        //transaction buttons
        val isButtonsEnabled = savingsTotal > 0
        bSubSavingsTransaction.isEnabled = isButtonsEnabled
        bTranferFromSavings.isEnabled = isButtonsEnabled

        //Button for adding to savings.
        bAddSavingsTransaction.setOnClickListener {
            intent.putExtra(Const.INTENT_KEY_TRANSACTION_TYPE, Const.INTENT_VALUE_ADD_TRANSACTION)
            intent.putExtra(Const.INTENT_KEY_SAVINGS_ID, id)
            activityResultLauncher.launch(intent)
        }

        //Button for subtracting from savings.
        bSubSavingsTransaction.setOnClickListener {
            intent.putExtra(Const.INTENT_KEY_TRANSACTION_TYPE, Const.INTENT_VALUE_SUB_TRANSACTION)
            intent.putExtra(Const.INTENT_KEY_SAVINGS_ID, id)
            activityResultLauncher.launch(intent)
        }

        //Button for Editing Savings
        val bEditSavingsDetails = findViewById<Button>(R.id.bEditMs)
        bEditSavingsDetails.setOnClickListener {
            val intent = Intent(this, SavingsEditActivity::class.java)
            intent.putExtra(Const.INTENT_KEY_SAVINGS_ID, id)
            activityResultLauncher.launch(intent)
        }

        //Button for transfer

        bTranferFromSavings.setOnClickListener {
            val intent = Intent(this, GeneralTransferActivity::class.java)
            intent.putExtra(Const.INTENT_KEY_SAVINGS_ID, id)
            intent.putExtra(Constants.INTENT_KEY_RQST_FR, Constants.INTENT_VAL_RQST_FR_SAVINGS)
            activityResultLauncher.launch(intent)
        }

        //Back Button
        val bBackToSavingsHome = findViewById<Button>(R.id.bBackSavings)
        bBackToSavingsHome.setOnClickListener {
            val intent = Intent(applicationContext, MainActivity::class.java)
            intent.putExtra(Constants.INTENT_KEY_NAVIGATE_TO, Constants.NAVIGATE_TO_SAVINGS)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            setResult(RESULT_OK)
            startActivity(intent)
            finish()
        }

    }

    private fun initializeSavingsTransactionData(savingsId:String){
        //initialize savings transaction list.
        savingsTransactionList = sqLiteHelper.getTransactionsOnFinancialObject(savingsId)
    }

    private fun initViews(){
        savingsName = findViewById(R.id.tvName)
        savingsGoalAmount = findViewById(R.id.tvGoalAmountMs)
        savingsAmount = findViewById(R.id.tvAmount)
        savingsDescription = findViewById(R.id.etDescriptionMs)
        savingsCompletedMark = findViewById(R.id.ivSavingsCompletedCheckMs)
        bAddSavingsTransaction = findViewById(R.id.bAddTransactionMs)
        bSubSavingsTransaction = findViewById(R.id.bSubtractTransactionMs)
        bTranferFromSavings = findViewById(R.id.bTransferTransactionMs)
    }
}