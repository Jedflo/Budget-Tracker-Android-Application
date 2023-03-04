package com.example.budgettracker.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.budgettracker.*
import com.example.budgettracker.databinding.FragmentHomeBinding
import java.math.BigDecimal

class HomeFragment : Fragment() {

    private lateinit var savingsAdapter: SavingsAdapter
    private lateinit var savingsRecyclerView : RecyclerView
    private lateinit var savingsList : ArrayList<SavingsModel>
    lateinit var savingsTitle : ArrayList<String>
    lateinit var savingsAmount : ArrayList<BigDecimal>
    private var _binding: FragmentHomeBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    var activityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){ result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK){
            val intent = result.data
            Toast.makeText(context,"TEST",Toast.LENGTH_SHORT).show()
        }
        else{
            println("SEARCH: SAVINGS CANCELLED")
            System.out.println("SAVINGS RESULT OK!")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setActivityTitle("Savings")
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        homeViewModel.text.observe(viewLifecycleOwner) {

        }

        //Add Savings Transition
        val bAddSavingsItem = root.findViewById<Button>(R.id.bAddSavings)
        bAddSavingsItem.setOnClickListener {
            val intent = Intent(context, SavingsAddActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            activityResultLauncher.launch(intent)
        }


        return root
    }

    fun Fragment.setActivityTitle(title: String) {
        (activity as AppCompatActivity?)?.supportActionBar?.title = title
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Put savings data in savingsList.
        initializeSavingsData()

        //Get layout manager.
        val layoutManager = LinearLayoutManager(context)
        //Get Recycler view.
        savingsRecyclerView = view.findViewById(R.id.rvSavingsList)
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
                val clickedItem = savingsList.get(position)
                val savingId = clickedItem.savingsId
                val savingName = clickedItem.savingsName
                val savingAmount = clickedItem.savingsAmouunt

                val intent = Intent(context, MainSavingsActivity::class.java)
                intent.putExtra("id", savingId)
                intent.putExtra("name", savingName)
                intent.putExtra("amount", savingAmount)
                activityResultLauncher.launch(intent)

            }

        })


    }

    private fun initializeSavingsData(){
        savingsList = arrayListOf<SavingsModel>()

        val mainFinancialObject: FinancialObject =
            FileManager.loadFinancialObject(context?.filesDir?.absolutePath,Constants.SAVINGS_FILENAME)
                ?: return

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


}