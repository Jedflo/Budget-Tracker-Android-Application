package com.example.budgettracker.ui.notifications

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
import com.example.budgettracker.databinding.FragmentNotificationsBinding
import java.math.BigDecimal

class NotificationsFragment : Fragment() {

    private lateinit var sqLiteHelper: SQLiteHelper
    private lateinit var debtAdapter: DebtAdapter
    private lateinit var debtRecyclerView: RecyclerView
    private lateinit var debtList: ArrayList<FinancialObjectModel>
    private var _binding: FragmentNotificationsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    var activityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK){
            val intent = result.data
            Log.d("DEBTS RESULT OK", intent.toString())
        }
        else{
            Log.d("DEBTS RESULT N", "back")
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setActivityTitle("Debts")
        val notificationsViewModel =
            ViewModelProvider(this).get(NotificationsViewModel::class.java)

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //Set database access.
        sqLiteHelper = SQLiteHelper(requireActivity().applicationContext)

        //Add Debt item
        val bAddDebtItem = root.findViewById<Button>(R.id.bAddDebt)
        bAddDebtItem.setOnClickListener {
            val intent = Intent(context, DebtAddActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
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
        //Put Debt data in debtList.
        initializeDebtData()

        //Get layout manager
        val layoutManager = LinearLayoutManager(context)
        //Get Recycler view.
        debtRecyclerView = view.findViewById(R.id.rvDebtList)
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
                val clickedItem = debtList[position]
                val debtId = clickedItem.id
                val intent = Intent(context, MainDebtActivity::class.java)
                intent.putExtra(Const.INTENT_KEY_DEBT_ID, debtId)
                activityResultLauncher.launch(intent)
            }
        })
    }

    private fun initializeDebtData(){
        debtList = sqLiteHelper.getFinancialObjects(Const.FO_TYPE_DEBT)
    }


}