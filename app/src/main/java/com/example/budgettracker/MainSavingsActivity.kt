package com.example.budgettracker

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.budgettracker.ui.home.HomeFragment
import java.math.BigDecimal


class MainSavingsActivity : AppCompatActivity() {
    private lateinit var savingsTransactionList: ArrayList<FinancialTransactionSavingsModel>
    private lateinit var savingsTransactionsRecyclerView: RecyclerView

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
        val savingsName: TextView = findViewById(R.id.tvName)
        val savingsGoalAmount: TextView = findViewById(R.id.tvGoalAmountMs)
        val savingsAmount: TextView = findViewById(R.id.tvAmount)
        val savingsDescription: EditText = findViewById(R.id.etDescriptionMs)
        val savingsCompletedMark: ImageView = findViewById(R.id.ivSavingsCompletedCheckMs)

        //Retrieve data from bundle
        val bundle : Bundle? = intent.extras
        val id = bundle!!.getString("id")

        //retrieve relevant info for savings amount text view and description edit text
        val financialSavings = FileManager.loadFinancialObject(
            applicationContext.filesDir.absolutePath,
            Constants.SAVINGS_FILENAME
        ).savingsObjects.get(id)

        //set name and goal amount text views
        savingsName.text = financialSavings?.name
        savingsGoalAmount.text = BigDecimalTools.prepareForPrint(financialSavings?.amount)
        savingsAmount.text = BigDecimalTools.prepareForPrint(
            financialSavings?.financialTransactionsTotal
        )
        savingsDescription.setText(financialSavings?.description)

        //Set title of activity
        setTitle(financialSavings?.name)

        //Check if savings has reached goal, if so make check mark visible.
        if(financialSavings?.amount?.compareTo(financialSavings?.financialTransactionsTotal)!! <=0){
            savingsCompletedMark.visibility = View.VISIBLE
        }

        //Use savings name to retrieve related FTs to populate savings financial transaction recycler view.
        initializeSavingsTransactionData(id)
        var financialTransactionsRecyclerView =findViewById<RecyclerView>(R.id.rvTransactionsMs)
        financialTransactionsRecyclerView.layoutManager = LinearLayoutManager(this)
        financialTransactionsRecyclerView.setHasFixedSize(true)
        savingsTransactionList.sortWith((
                Comparator.comparing(
                    FinancialTransactionSavingsModel::ftsTransacDate
                )).reversed())
        var financialSavingsAdapter = FinancialTransactionSavingsAdapter(savingsTransactionList)
        financialTransactionsRecyclerView.adapter = financialSavingsAdapter

        financialSavingsAdapter.setOnItemClickListener(object : FinancialTransactionSavingsAdapter.onItemClickListener{
            override fun onItemClick(position: Int) {
                val clickedItem = savingsTransactionList.get(position)
                clickedItem.ftsId
                Toast.makeText(
                    this@MainSavingsActivity,
                    "Transaction ID: " + clickedItem.ftsId,
                    Toast.LENGTH_SHORT
                ).show()
            }

        })

        val bAddSavingsTransaction = findViewById<Button>(R.id.bAddTransactionMs)
        val bSubSavingsTransaction = findViewById<Button>(R.id.bSubtractTransactionMs)
        val intent = Intent(this, SavingsCreateTransactionActivity::class.java)

        val savingsTotal = financialSavings.financialTransactionsTotal
        bSubSavingsTransaction.isEnabled = savingsTotal?.compareTo(BigDecimal.ZERO)!!>0

        //Button for adding to savings.
        bAddSavingsTransaction.setOnClickListener {
            intent.putExtra("transaction type", "add")
            intent.putExtra("id", id)
            activityResultLauncher.launch(intent)
        }

        //Button for subtracting from savings.
        bSubSavingsTransaction.setOnClickListener {
            intent.putExtra("transaction type", "sub")
            intent.putExtra("id", id)
            activityResultLauncher.launch(intent)
        }

        //Button for Editing Savings
        val bEditSavingsDetails = findViewById<Button>(R.id.bEditMs)
        bEditSavingsDetails.setOnClickListener {
            val intent = Intent(this, SavingsEditActivity::class.java)
            intent.putExtra("id", id)
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

    private fun initializeSavingsTransactionData(savingsId:String?){
        //initialize savings transaction list.
        savingsTransactionList = arrayListOf<FinancialTransactionSavingsModel>()
        val fo: FinancialObject
        fo = FileManager.loadFinancialObject(
            applicationContext.filesDir.absolutePath,
            Constants.SAVINGS_FILENAME
        )
        val savingsFinancialTransactions = fo.savingsObjects.get(savingsId)?.transactions
            ?: return

        for(financialTransaction in savingsFinancialTransactions.values){
            val savingsFT = FinancialTransactionSavingsModel(
                financialTransaction.financialTransactionID,
                financialTransaction.name,
                financialTransaction.amount,
                financialTransaction.dateCreated
            )
            savingsTransactionList.add(savingsFT)
        }
    }
}