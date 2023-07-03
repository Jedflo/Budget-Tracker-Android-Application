package com.example.budgettracker

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.budgettracker.ui.dashboard.WalletAdapter
import com.example.budgettracker.ui.dashboard.WalletModel
import com.example.budgettracker.ui.home.SavingsAdapter
import com.example.budgettracker.ui.home.SavingsModel
import com.example.budgettracker.ui.notifications.DebtAdapter
import com.example.budgettracker.ui.notifications.DebtModel
import java.math.BigDecimal

class GeneralTransferActivity : AppCompatActivity() {
    private lateinit var walletAdapter: WalletAdapter
    private lateinit var walletRecyclerView: RecyclerView
    private lateinit var walletList: ArrayList<FinancialObjectModel>
    private lateinit var savingsAdapter: SavingsAdapter
    private lateinit var savingsRecyclerView : RecyclerView
    private lateinit var savingsList : ArrayList<SavingsModel>
    private lateinit var debtAdapter: DebtAdapter
    private lateinit var debtRecyclerView: RecyclerView
    private lateinit var debtList: ArrayList<DebtModel>
    private lateinit var tvTransferFromSelectedItemName: TextView
    private lateinit var tvTransferToSelectedItemName: TextView
    private lateinit var bTransfer: Button
    private var selectedItemObjectNameFrom:String? = null;
    private var selectedItemObjectIdFrom:String? = null;
    private var selectedItemFinancialObjectTypeFrom: String? = null;
    private var selectedItemObjectNameTo:String? = null;
    private var selectedItemObjectIdTo:String? = null;
    private var selectedItemFinancialObjectTypeTo: String? = null;


//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_general_transfer)
//        bTransfer= findViewById<Button>(R.id.bTransfer)
//        //For when back gesture or button is triggered will return to Main[financial obj]Activity
//        val callback = onBackPressedDispatcher.addCallback(this) {
//            setResult(RESULT_OK)
//            finish()
//        }
//
//        tvTransferFromSelectedItemName = findViewById<TextView>(R.id.tvTransferFromSelectedName)
//        tvTransferToSelectedItemName = findViewById<TextView>(R.id.tvTransferToSelectedName)
//
//        //Fetch spinner options.
//        val financialObjectTypes = resources.getStringArray(R.array.FinancialObjectTypes)
//        val fromSpinner = findViewById<Spinner>(R.id.spTransferFrom)
//        val fromRecyclerView = findViewById<RecyclerView>(R.id.rvTransferFrom)
//        val toSpinner = findViewById<Spinner>(R.id.spTransferTo)
//        val toRecyclerView = findViewById<RecyclerView>(R.id.rvTransferTo)
//
//        //Fetch intent from activity
//        val bundle: Bundle? = intent.extras
//        val requestFrom = bundle!!.getString(Constants.INTENT_KEY_RQST_FR)
//        selectedItemFinancialObjectTypeFrom = requestFrom
//
//        if (bundle!!.getString("wallet id") != null){
//            selectedItemObjectIdFrom = bundle!!.getString("wallet id")
//            selectedItemObjectNameFrom = getWalletFromId(selectedItemObjectIdFrom)?.name
//            selectedItemFinancialObjectTypeFrom = Constants.WLLT_TYPE
//            tvTransferFromSelectedItemName.text = selectedItemObjectNameFrom
//            changeTextViewBgColorIfDarkMode(
//                tvTransferFromSelectedItemName,
//                R.color.wallet_item_blue_dark,
//                R.color.wallet_item_blue
//            )
//
//        }
//        else if (bundle!!.getString("id") != null){
//            selectedItemObjectIdFrom = bundle!!.getString("id")
//            selectedItemObjectNameFrom = getSavingsFromId(selectedItemObjectIdFrom)?.name
//            selectedItemFinancialObjectTypeFrom = Constants.SVNG_TYPE
//            tvTransferFromSelectedItemName.text = selectedItemObjectNameFrom
//            tvTransferFromSelectedItemName.setBackgroundResource(R.color.savings_item_green)
//            changeTextViewBgColorIfDarkMode(
//                tvTransferFromSelectedItemName,
//                R.color.savings_item_green_dark,
//                R.color.savings_item_green
//            )
//        }
//        else if (bundle!!.getString("debtId") != null){
//            selectedItemObjectIdFrom = bundle!!.getString("debtId")
//            selectedItemObjectNameFrom = getDebtFromId(selectedItemObjectIdFrom)?.name
//            selectedItemFinancialObjectTypeFrom = Constants.DEBT_TYPE
//            tvTransferFromSelectedItemName.text = selectedItemObjectNameFrom
//            tvTransferFromSelectedItemName.setBackgroundResource(R.color.debt_item_yellow)
//            changeTextViewBgColorIfDarkMode(
//                tvTransferFromSelectedItemName,
//                R.color.debt_item_yellow_dark,
//                R.color.debt_item_yellow
//            )
//        }
//
//
//        if (fromSpinner != null){
//            val adapter = ArrayAdapter(
//                this,
//                android.R.layout.simple_spinner_dropdown_item,
//                financialObjectTypes
//            )
//            fromSpinner.adapter = adapter
//
//            //Select Appropriate Financial Object Type based on where the request came from.
//            when(requestFrom){
//                Constants.INTENT_VAL_RQST_FR_SAVINGS -> fromSpinner.setSelection(0)
//                Constants.INTENT_VAL_RQST_FR_WALLET -> fromSpinner.setSelection(1)
//                Constants.INTENT_VAL_RQST_FR_DEBT -> fromSpinner.setSelection(2)
//            }
//
//
//            fromSpinner.onItemSelectedListener = object :
//            AdapterView.OnItemSelectedListener{
//                override fun onItemSelected(
//                    parent: AdapterView<*>,
//                    view: View,
//                    position: Int,
//                    id: Long
//                ) {
//                    if (financialObjectTypes[position].equals(
//                            Constants.INTENT_VAL_RQST_FR_SAVINGS
//                        )){
//                        populateRecyclerViewWithSavings(
//                            fromRecyclerView,
//                            tvTransferFromSelectedItemName,
//                            Constants.TRANSFER_FROM
//                        )
//                    }
//                    else if (financialObjectTypes[position].equals(
//                            Constants.INTENT_VAL_RQST_FR_WALLET
//                        )){
//                        populateRecyclerViewWithWallets(
//                            fromRecyclerView,
//                            tvTransferFromSelectedItemName,
//                            Constants.TRANSFER_FROM
//                        )
//
//                    }else if (financialObjectTypes[position].equals(
//                            Constants.INTENT_VAL_RQST_FR_DEBT
//                        )){
//                        populateRecyclerViewWithDebt(
//                            fromRecyclerView,
//                            tvTransferFromSelectedItemName,
//                            Constants.TRANSFER_FROM
//                        )
//                    }
//                } //onItemSelected
//
//                override fun onNothingSelected(parent: AdapterView<*>?) {
//                    TODO("Not yet implemented")
//                }
//            }
//        }
//
//        if (toSpinner != null) {
//            val adapter = ArrayAdapter(
//                this,
//                android.R.layout.simple_spinner_dropdown_item,
//                financialObjectTypes
//            )
//            toSpinner.adapter = adapter
//
//            toSpinner.onItemSelectedListener = object :
//            AdapterView.OnItemSelectedListener{
//                override fun onItemSelected(
//                    parent: AdapterView<*>,
//                    view: View,
//                    position: Int,
//                    id: Long
//                ) {
//                    if (financialObjectTypes[position].equals(
//                            Constants.INTENT_VAL_RQST_FR_SAVINGS
//                        )){
//                        populateRecyclerViewWithSavings(
//                            toRecyclerView,
//                            tvTransferToSelectedItemName,
//                            Constants.TRANSFER_TO
//                        )
//                    }
//                    else if (financialObjectTypes[position].equals(
//                            Constants.INTENT_VAL_RQST_FR_WALLET
//                        )){
//                        populateRecyclerViewWithWallets(
//                            toRecyclerView,
//                            tvTransferToSelectedItemName,
//                            Constants.TRANSFER_TO
//                        )
//
//                    }else if (financialObjectTypes[position].equals(
//                            Constants.INTENT_VAL_RQST_FR_DEBT
//                        )){
//                        populateRecyclerViewWithDebt(
//                            toRecyclerView,
//                            tvTransferToSelectedItemName,
//                            Constants.TRANSFER_TO
//                        )
//                    }
//                } //onItemSelected
//
//                override fun onNothingSelected(parent: AdapterView<*>?) {
//                    bTransfer.isEnabled = false
//                }
//            }
//        }
//
//        val etTransferTransactionName = findViewById<EditText>(R.id.etTransferTransactionLabel)
//        val etTransferTransactionAmount = findViewById<EditText>(R.id.etTransferTRansactionAmount)
//        val alertDialogBuilder = AlertDialog.Builder(this)
//
//        bTransfer.setOnClickListener {
//            alertDialogBuilder.setTitle("Missing Information")
//            alertDialogBuilder.setPositiveButton("Ok"){ dialog, which -> dialog.dismiss() }
//
//
//            if (etTransferTransactionName.text.toString().isNullOrBlank()){
//                alertDialogBuilder.setMessage("Transaction name cannot be blank")
//                alertDialogBuilder.show()
//                return@setOnClickListener
//            }
//
//            if (etTransferTransactionAmount.text.toString().isNullOrBlank()){
//                alertDialogBuilder.setMessage("Transaction amount cannot be blank")
//                alertDialogBuilder.show()
//                return@setOnClickListener
//            }
//
//            val transferTransactionName = etTransferTransactionName.text.toString()
//            val transferTransactionAmountString = etTransferTransactionAmount.text.toString()
//            val transferTransactionAmount = BigDecimal(transferTransactionAmountString)
//            var objectFinancialTransactionTotal:BigDecimal? = BigDecimal(0)
//
//
//
//            //Get the financial object's total.
//            if (selectedItemFinancialObjectTypeFrom.equals(Constants.SVNG_TYPE)){
//                objectFinancialTransactionTotal = getSavingsFromId(
//                    selectedItemObjectIdFrom
//                )?.financialTransactionsTotal
//            }
//            else if(selectedItemFinancialObjectTypeFrom.equals(Constants.WLLT_TYPE)){
//                objectFinancialTransactionTotal = getWalletFromId(
//                    selectedItemObjectIdFrom
//                )?.financialTransactionsTotal
//            }
//            else if(selectedItemFinancialObjectTypeFrom.equals(Constants.DEBT_TYPE)){
//                objectFinancialTransactionTotal = getDebtFromId(
//                    selectedItemObjectIdFrom
//                )?.financialTransactionsTotal
//            }
//
//            //Check if financial object total > transfer amount.
//            try {
//                BigDecimalTools.safeSubtract(
//                    objectFinancialTransactionTotal,
//                    transferTransactionAmount
//                )
//            } catch (e: ArithmeticException){
//                val financialObjTotal = BigDecimalTools.prepareForPrint(
//                    objectFinancialTransactionTotal
//                )
//                val transferTransacAmount = BigDecimalTools.prepareForPrint(
//                    transferTransactionAmount
//                )
//                alertDialogBuilder.setTitle("Error")
//                alertDialogBuilder.setMessage(
//                    "$selectedItemObjectNameFrom total is $financialObjTotal. Cannot " +
//                            "take $transferTransacAmount from it. "
//                )
//                alertDialogBuilder.show()
//                return@setOnClickListener
//            }
//
//
//
//
//            //Alert dialog for transfer.
//            alertDialogBuilder.setTitle("Confirm Transfer")
//            alertDialogBuilder.setMessage(
//                "Transfer $transferTransactionAmountString " +
//                        "From $selectedItemObjectNameFrom To " +
//                        "$selectedItemObjectNameTo ?"
//            )
//
//            alertDialogBuilder.setPositiveButton("Cancel"){ dialog, which -> dialog.dismiss() }
//
//            alertDialogBuilder.setNegativeButton("Transfer"){ dialog, which ->
//                //Creating a subtract FT for financial object where the transfer will be coming from
//                createFinancialTransactionForFinancialObject(
//                    selectedItemObjectIdFrom,
//                    selectedItemFinancialObjectTypeFrom,
//                    "[To $selectedItemObjectNameTo] " + transferTransactionName,
//                    transferTransactionAmount.negate()
//                )
//                //Creating an add FT for financial object which is the recipient of the transfer.
//                createFinancialTransactionForFinancialObject(
//                    selectedItemObjectIdTo,
//                    selectedItemFinancialObjectTypeTo,
//                    "[Fr $selectedItemObjectNameFrom] " + transferTransactionName,
//                    transferTransactionAmount
//                )
//                setResult(RESULT_OK)
//                finish()
//            }
//
//            alertDialogBuilder.show()
//        }
//
//
//    }

    private fun initializeWalletData(){
        walletList = arrayListOf<FinancialObjectModel>()

//        val mainFinancialObject: FinancialObject =
//            FileManager.loadFinancialObject(
//                applicationContext.filesDir.absolutePath,
//                Constants.SAVINGS_FILENAME)?: return
//
//        val walletMap: HashMap<String,FinancialObject> = mainFinancialObject.childFinancialObjects
//        val numberFormatter: NumberFormatter = NumberFormatter()
//        for(wallet in walletMap){
//            wallet.key
//            wallet.value
//            val wallet = WalletModel(
//                wallet.key,
//                wallet.value.name,
//                numberFormatter.formatNumber(wallet.value.financialTransactionsTotal)
//            )
//            walletList.add(wallet)
//        }


    }

    private fun populateRecyclerViewWithWallets(recyclerView: RecyclerView,
                                                affectedTextView: TextView,
                                                toOrFromMode:Int){
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
//        walletAdapter.setOnItemClickListener(object: WalletAdapter.onItemClickListener{
//            override fun onItemClick(position: Int) {
//                val selectedItem = walletList[position]
//                affectedTextView.text = selectedItem.walletName
//                changeTextViewBgColorIfDarkMode(
//                    affectedTextView,
//                    R.color.wallet_item_blue_dark,
//                    R.color.wallet_item_blue
//                )
//
//                if (toOrFromMode == Constants.TRANSFER_FROM){
//                    selectedItemObjectNameFrom = selectedItem.walletName
//                    selectedItemObjectIdFrom = selectedItem.walletId
//                    selectedItemFinancialObjectTypeFrom = Constants.WLLT_TYPE
//                    disableTransferButtonIfFromAndToSame()
//                }
//                else if (toOrFromMode == Constants.TRANSFER_TO){
//                    selectedItemObjectNameTo = selectedItem.walletName
//                    selectedItemObjectIdTo = selectedItem.walletId
//                    selectedItemFinancialObjectTypeTo = Constants.WLLT_TYPE
//                    disableTransferButtonIfFromAndToSame()
//                }
//
//            }
//        })
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

//    private fun populateRecyclerViewWithSavings(recyclerView: RecyclerView,
//                                                affectedTextView: TextView,
//                                                toOrFromMode: Int){
//        initializeSavingsData()
//
//        //Get layout manager.
//        val layoutManager = LinearLayoutManager(this)
//        //Get Recycler view.
//        savingsRecyclerView = recyclerView
//        //Set RecyclerView's layout manager.
//        savingsRecyclerView.layoutManager = layoutManager
//        savingsRecyclerView.setHasFixedSize(false)
//
//        //Use the savingsAdapter on savingsList
//        savingsAdapter = SavingsAdapter(savingsList)
//        //Populate Recycler view with data.
//        savingsRecyclerView.adapter = savingsAdapter
//        //Set on click listener on recycler view elements.
//        savingsAdapter.setOnItemClickListener(object : SavingsAdapter.onItemClickListener{
//            override fun onItemClick(position: Int) {
//                val selectedItem = savingsList[position]
//                affectedTextView.text = selectedItem.savingsName
//                changeTextViewBgColorIfDarkMode(
//                    affectedTextView,
//                    R.color.savings_item_green_dark,
//                    R.color.savings_item_green
//                )
//
//                if (toOrFromMode == Constants.TRANSFER_FROM) {
//                    selectedItemObjectNameFrom = selectedItem.savingsName
//                    selectedItemObjectIdFrom = selectedItem.savingsId
//                    selectedItemFinancialObjectTypeFrom = Constants.SVNG_TYPE
//                    disableTransferButtonIfFromAndToSame()
//                }
//                else if(toOrFromMode == Constants.TRANSFER_TO){
//                    selectedItemObjectNameTo = selectedItem.savingsName
//                    selectedItemObjectIdTo = selectedItem.savingsId
//                    selectedItemFinancialObjectTypeTo = Constants.SVNG_TYPE
//                    disableTransferButtonIfFromAndToSame()
//                }
//
//            }
//        })
//    }

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

    private fun populateRecyclerViewWithDebt(recyclerView: RecyclerView,
                                             affectedTextView: TextView,
                                             toOrFromMode: Int){
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
                affectedTextView.text = selectedItem.DebtName
                changeTextViewBgColorIfDarkMode(
                    affectedTextView,
                    R.color.debt_item_yellow_dark,
                    R.color.debt_item_yellow
                )

                if (toOrFromMode == Constants.TRANSFER_FROM) {
                    selectedItemObjectNameFrom = selectedItem.DebtName
                    selectedItemObjectIdFrom = selectedItem.DebtId
                    selectedItemFinancialObjectTypeFrom = Constants.DEBT_TYPE
                    disableTransferButtonIfFromAndToSame()
                }
                else if(toOrFromMode == Constants.TRANSFER_TO){
                    selectedItemObjectNameTo = selectedItem.DebtName
                    selectedItemObjectIdTo = selectedItem.DebtId
                    selectedItemFinancialObjectTypeTo = Constants.DEBT_TYPE
                    disableTransferButtonIfFromAndToSame()
                }
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

    private fun getWallets(): HashMap<String, FinancialObject>? {
        val financialObject = FileManager.loadFinancialObject(
            applicationContext.filesDir.absolutePath,
            Constants.SAVINGS_FILENAME
        )
        return financialObject.childFinancialObjects
    }

    private fun getSavingsFromId(savingsId:String?): FinancialSavings? {
        val financialObject = FileManager.loadFinancialObject(
            applicationContext.filesDir.absolutePath,
            Constants.SAVINGS_FILENAME
        )

        return financialObject.savingsObjects.get(savingsId)
    }

    private fun getSavings(): HashMap<String, FinancialSavings>? {
        val financialObject = FileManager.loadFinancialObject(
            applicationContext.filesDir.absolutePath,
            Constants.SAVINGS_FILENAME
        )
        return financialObject.savingsObjects
    }

    private fun getDebtFromId(debtId:String?): FinancialDebt? {
        val financialObject = FileManager.loadFinancialObject(
            applicationContext.filesDir.absolutePath,
            Constants.SAVINGS_FILENAME
        )
        return financialObject.debtObjects.get(debtId)
    }

    private fun getDebts(): HashMap<String, FinancialDebt>? {
        val financialObject = FileManager.loadFinancialObject(
            applicationContext.filesDir.absolutePath,
            Constants.SAVINGS_FILENAME
        )
        return financialObject.debtObjects
    }

    private fun disableTransferButtonIfFromAndToSame(){
        bTransfer.isEnabled = !(
                        selectedItemObjectIdFrom.isNullOrEmpty() ||
                        selectedItemObjectIdTo.isNullOrEmpty() ||
                        selectedItemObjectIdFrom.equals(selectedItemObjectIdTo)
                )
    }

    private fun createFinancialTransactionForFinancialObject(
        financialObjectId: String?,
        financialObjectType: String?,
        transactionName: String,
        amount: BigDecimal) {
        val financialEntity = FileManager.loadFinancialObject(
            applicationContext.filesDir.absolutePath,
            Constants.SAVINGS_FILENAME
        )

        if (financialObjectType.equals(Constants.SVNG_TYPE)){
            val savings = financialEntity.savingsObjects.get(financialObjectId)
            savings?.createFinancialTransaction(transactionName, "", amount)

        }
        else if (financialObjectType.equals(Constants.WLLT_TYPE)){
            val wallet = financialEntity.childFinancialObjects.get(financialObjectId)
            wallet?.createFinancialTransaction(transactionName, "", amount)
        }
        else if (financialObjectType.equals(Constants.DEBT_TYPE)) {
            val debt = financialEntity.debtObjects.get(financialObjectId)
            debt?.createFinancialTransaction(transactionName, "", amount)
        }

        FileManager.saveFinancialObject(
            financialEntity,
            applicationContext.filesDir.absolutePath,
            Constants.SAVINGS_FILENAME
        )

    }

    /**
     * Changes the background resource of a given text view depending if the app is in dark mode or not
     * @param affectedTextView textview whose background will be changed
     * @param bgDarkResId resource id of color that will be used for dark theme.
     * @param bgLightResId resource id of color that will be used for light theme.
     */
    private fun changeTextViewBgColorIfDarkMode(affectedTextView:TextView, bgDarkResId: Int, bgLightResId: Int){
        val nightModeFlags: Int = applicationContext.resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK
        if(nightModeFlags == Configuration.UI_MODE_NIGHT_YES){
            affectedTextView.setBackgroundResource(bgDarkResId)
        }
        else{
            affectedTextView.setBackgroundResource(bgLightResId)
        }
    }




}