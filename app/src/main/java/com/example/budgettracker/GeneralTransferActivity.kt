package com.example.budgettracker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Adapter
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.budgettracker.ui.dashboard.WalletAdapter
import com.example.budgettracker.ui.dashboard.WalletModel
import com.example.budgettracker.ui.home.SavingsAdapter
import com.example.budgettracker.ui.home.SavingsModel
import com.example.budgettracker.ui.notifications.DebtAdapter
import com.example.budgettracker.ui.notifications.DebtModel

class GeneralTransferActivity : AppCompatActivity() {
    private lateinit var walletAdapter: WalletAdapter
    private lateinit var walletRecyclerView: RecyclerView
    private lateinit var walletList: ArrayList<WalletModel>
    private lateinit var savingsAdapter: SavingsAdapter
    private lateinit var savingsRecyclerView : RecyclerView
    private lateinit var savingsList : ArrayList<SavingsModel>
    private lateinit var debtAdapter: DebtAdapter
    private lateinit var debtRecyclerView: RecyclerView
    private lateinit var debtList: ArrayList<DebtModel>
    private lateinit var tvTransferFromSelectedItemName: TextView
    private var selectedItemObjectId:String? = null;
    private var selectedItemFinancialObjectType: String? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_general_transfer)
        //For when back gesture or button is triggered will return to MainWalletActivity
        val callback = onBackPressedDispatcher.addCallback(this) {
            setResult(RESULT_OK)
            finish()
        }

        tvTransferFromSelectedItemName = findViewById<TextView>(R.id.tvTransferFromSelectedName)

        //Fetch spinner options.
        val financialObjectTypes = resources.getStringArray(R.array.FinancialObjectTypes)
        val fromSpinner = findViewById<Spinner>(R.id.spTransferFrom)
        val fromRecyclerView = findViewById<RecyclerView>(R.id.rvTransferFrom)

        //Fetch intent from activity
        val bundle: Bundle? = intent.extras
        val requestFrom = bundle!!.getString(Constants.INTENT_KEY_RQST_FR)
        selectedItemFinancialObjectType = requestFrom

        if (bundle!!.getString("wallet id") != null){
            selectedItemObjectId = bundle!!.getString("wallet id")
            tvTransferFromSelectedItemName.text = getWalletFromId(selectedItemObjectId)?.name
        }
        else if (bundle!!.getString("id") != null){
            selectedItemObjectId = bundle!!.getString("id")
            tvTransferFromSelectedItemName.text = getSavingsFromId(selectedItemObjectId)?.name
        }
        else if (bundle!!.getString("debtId") != null){
            selectedItemObjectId = bundle!!.getString("debtId")
            tvTransferFromSelectedItemName.text = getDebtFromId(selectedItemObjectId)?.name
        }


        if (fromSpinner != null){
            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                financialObjectTypes
            )
            fromSpinner.adapter = adapter

            //Select Appropriate Financial Object Type based on where the request came from.
            when(requestFrom){
                Constants.INTENT_VAL_RQST_FR_SAVINGS -> fromSpinner.setSelection(0)
                Constants.INTENT_VAL_RQST_FR_WALLET -> fromSpinner.setSelection(1)
                Constants.INTENT_VAL_RQST_FR_DEBT -> fromSpinner.setSelection(2)
            }


            fromSpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener{
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    if (financialObjectTypes[position].equals(
                            Constants.INTENT_VAL_RQST_FR_SAVINGS
                        )){
                        populateRecyclerViewWithSavings(fromRecyclerView)
                    }
                    else if (financialObjectTypes[position].equals(
                            Constants.INTENT_VAL_RQST_FR_WALLET
                        )){
                        populateRecyclerViewWithWallets(fromRecyclerView)

                    }else if (financialObjectTypes[position].equals(
                            Constants.INTENT_VAL_RQST_FR_DEBT
                        )){
                        populateRecyclerViewWithDebt(fromRecyclerView)
                    }
                } //onItemSelected

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }
            }
        }

    }

    private fun initializeWalletData(){
        walletList = arrayListOf<WalletModel>()

        val mainFinancialObject: FinancialObject =
            FileManager.loadFinancialObject(
                applicationContext.filesDir.absolutePath,
                Constants.SAVINGS_FILENAME)?: return

        val walletMap: HashMap<String,FinancialObject> = mainFinancialObject.childFinancialObjects
        val numberFormatter: NumberFormatter = NumberFormatter()
        for(wallet in walletMap){
            wallet.key
            wallet.value
            val wallet = WalletModel(
                wallet.key,
                wallet.value.name,
                numberFormatter.formatNumber(wallet.value.financialTransactionsTotal)
            )
            walletList.add(wallet)
        }


    }

    private fun populateRecyclerViewWithWallets(recyclerView: RecyclerView){
        //Initialize wallets data in wallets list
        initializeWalletData()

        //Get layout manager
        val layoutManager = LinearLayoutManager(this)
        //Get RecyclerView
        walletRecyclerView = recyclerView
        //Set RecyclerView's layout manager
        walletRecyclerView.layoutManager = layoutManager
        walletRecyclerView.setHasFixedSize(false)

        //Use the walletAdapter on walletList
        walletAdapter = WalletAdapter(walletList)
        //Populate RecyclerView with data.
        walletRecyclerView.adapter = walletAdapter
        //Set onClickListener on RecyclerView elements
        walletAdapter.setOnItemClickListener(object: WalletAdapter.onItemClickListener{
            override fun onItemClick(position: Int) {
                val selectedItem = walletList[position]
                tvTransferFromSelectedItemName.text = selectedItem.walletName
                selectedItemObjectId = selectedItem.walletId
                selectedItemFinancialObjectType = Constants.WLLT_TYPE
            }
        })
    }

    private fun initializeSavingsData(){
        savingsList = arrayListOf<SavingsModel>()

        val mainFinancialObject: FinancialObject =
            FileManager.loadFinancialObject(
                applicationContext.filesDir?.absolutePath,
                Constants.SAVINGS_FILENAME
            ) ?: return

        val savingsMap: HashMap<String, FinancialSavings> = mainFinancialObject.savingsObjects
        val numberFormatter: NumberFormatter = NumberFormatter()
        for (financialSaving in savingsMap){
            val savings = SavingsModel(
                financialSaving.value.financialObjectID,
                financialSaving.value.name,
                financialSaving.value.description,
                numberFormatter.formatNumber(financialSaving.value.financialTransactionsTotal),
                financialSaving.value.status,
                numberFormatter.formatNumber(financialSaving.value.amount))
            savingsList.add(savings)
        }
    }

    private fun populateRecyclerViewWithSavings(recyclerView: RecyclerView){
        initializeSavingsData()

        //Get layout manager.
        val layoutManager = LinearLayoutManager(this)
        //Get Recycler view.
        savingsRecyclerView = recyclerView
        //Set RecyclerView's layout manager.
        savingsRecyclerView.layoutManager = layoutManager
        savingsRecyclerView.setHasFixedSize(false)

        //Use the savingsAdapter on savingsList
        savingsAdapter = SavingsAdapter(savingsList)
        //Populate Recycler view with data.
        savingsRecyclerView.adapter = savingsAdapter
        //Set on click listener on recycler view elements.
        savingsAdapter.setOnItemClickListener(object : SavingsAdapter.onItemClickListener{
            override fun onItemClick(position: Int) {
                val selectedItem = savingsList[position]
                tvTransferFromSelectedItemName.text = selectedItem.savingsName
                selectedItemObjectId = selectedItem.savingsId
                selectedItemFinancialObjectType = Constants.SVNG_TYPE
            }
        })
    }

    private fun initializeDebtData(){
        debtList = arrayListOf()

        val mainFinancialObject: FinancialObject = FileManager.loadFinancialObject(
            applicationContext.filesDir?.absolutePath,
            Constants.SAVINGS_FILENAME
        )?: return

        val debtMap: HashMap <String, FinancialDebt> = mainFinancialObject.debtObjects
        val numberFormatter: NumberFormatter = NumberFormatter()
        for (financialDebt in debtMap){
            val debt = DebtModel(
                financialDebt.value.financialObjectID,
                financialDebt.value.name,
                numberFormatter.formatNumber(financialDebt.value.amount),
                numberFormatter.formatNumber(financialDebt.value.financialTransactionsTotal),
                financialDebt.value.status
            )
            debtList.add(debt)
        }
    }

    private fun populateRecyclerViewWithDebt(recyclerView: RecyclerView){
        initializeDebtData()

        //Get layout manager
        val layoutManager = LinearLayoutManager(this)
        //Get Recycler view.
        debtRecyclerView = recyclerView
        //Set RecyclerViews layout manager.
        debtRecyclerView.layoutManager = layoutManager
        debtRecyclerView.setHasFixedSize(false)

        //Use debtAdapter on debtList
        debtAdapter = DebtAdapter(debtList)
        //Populate RecyclerView with data.
        debtRecyclerView.adapter=debtAdapter
        //Set on click listener on recycler view elements.
        debtAdapter.setOnItemClickListener(object : DebtAdapter.onItemClickListener{
            override fun onItemClick(position: Int) {
                val selectedItem = debtList[position]
                tvTransferFromSelectedItemName.text = selectedItem.DebtName
                selectedItemObjectId = selectedItem.DebtId
                selectedItemFinancialObjectType = Constants.DEBT_TYPE
            }
        })
    }

    private fun getWalletFromId(walletId:String?): FinancialObject? {
        val financialObject = FileManager.loadFinancialObject(
            applicationContext.filesDir.absolutePath,
            Constants.SAVINGS_FILENAME
        )

        return financialObject.childFinancialObjects.get(walletId)
    }

    private fun getSavingsFromId(savingsId:String?): FinancialSavings? {
        val financialObject = FileManager.loadFinancialObject(
            applicationContext.filesDir.absolutePath,
            Constants.SAVINGS_FILENAME
        )

        return financialObject.savingsObjects.get(savingsId)
    }

    private fun getDebtFromId(debtId:String?): FinancialDebt? {
        val financialObject = FileManager.loadFinancialObject(
            applicationContext.filesDir.absolutePath,
            Constants.SAVINGS_FILENAME
        )

        return financialObject.debtObjects.get(debtId)
    }






}