package com.example.budgettracker.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.budgettracker.*
import com.example.budgettracker.databinding.FragmentDashboardBinding
import java.math.BigDecimal

class DashboardFragment : Fragment() {

    private lateinit var mySQLiteHelper: SQLiteHelper
    private lateinit var walletAdapter: WalletAdapter
    private lateinit var walletRecyclerView: RecyclerView
    private lateinit var walletList: ArrayList<FinancialObjectModel>
    lateinit var walletNames: ArrayList<String>
    lateinit var walletAmounts: ArrayList<BigDecimal>
    private var _binding: FragmentDashboardBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    var activityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK){
            val intent = result.data
            Log.d("WALLET RESULT OK", intent.toString())
        }
        else{
            Log.d("WALLET RESULT N", "back")
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setActivityTitle("Wallets")
        val dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        mySQLiteHelper = SQLiteHelper(requireActivity().applicationContext)
        //Create Wallet Transition
        val bCreateWallet = root.findViewById<Button>(R.id.bAddWallet)
        bCreateWallet.setOnClickListener {
            val intent = Intent(context, WalletAddActivity::class.java)
            activityResultLauncher.launch(intent)
        }

        return root
    }

    private fun setActivityTitle(title: String) {
        (activity as AppCompatActivity?)?.supportActionBar?.title = title
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Initialize wallets data in wallets list
        initializeWalletData()

        //Get layout manager
        val layoutManager = LinearLayoutManager(context)
        //Get RecyclerView
        walletRecyclerView = view.findViewById(R.id.rvWalletList)
        //Set RecyclerView's layout manager
        walletRecyclerView.layoutManager = layoutManager
        walletRecyclerView.setHasFixedSize(false)
        //Sort wallet by creation date.
        walletList.sortWith((
                Comparator.comparing(
                    FinancialObjectModel::dateCreated
                )).reversed())
        //Use the walletAdapter on walletList
        walletAdapter = WalletAdapter(walletList)
        //Populate RecyclerView with data.
        walletRecyclerView.adapter = walletAdapter
        //Set onClickListener on RecyclerView elements
        walletAdapter.setOnItemClickListener(object: WalletAdapter.onItemClickListener{
            override fun onItemClick(position: Int) {
                val clickedItem = walletList[position]
                val walletId = clickedItem.id
                val intent = Intent(context, MainWalletActivity::class.java)
                intent.putExtra(Const.INTENT_KEY_WALLET_ID, walletId)
                activityResultLauncher.launch(intent)
            }
        })

    }

//    private fun initializeWalletData(){
//        walletList = arrayListOf<WalletModel>()
//
//        val mainFinancialObject: FinancialObject =
//            FileManager.loadFinancialObject(
//                context?.filesDir?.absolutePath,
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
//
//
//    }


    private fun initializeWalletData(){
        walletList = mySQLiteHelper.getFinancialObjects(Const.FO_TYPE_WALLET)
    }






}