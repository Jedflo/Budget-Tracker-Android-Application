package com.example.budgettracker.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.budgettracker.R
import java.math.BigDecimal

class SavingsAdapter(private val savingsList : ArrayList<SavingsModel>) : RecyclerView.Adapter<SavingsAdapter.MyViewHolder>() {

    private lateinit var mListener : onItemClickListener

    interface onItemClickListener{
        fun onItemClick(position : Int)
    }

    fun setOnItemClickListener(listener : onItemClickListener){

        mListener = listener

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_savings_items, parent, false)
        return MyViewHolder(itemView, mListener)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = savingsList[position]
        holder.tvSavingsTitle.setText(currentItem.savingsName)
        holder.tvSavingsAmount.setText(currentItem.savingsAmouunt)
        holder.tvGoalAmount.setText(currentItem.savingsGoalAmount)

        val savingsEarned = BigDecimal(currentItem.savingsAmouunt.replace(",",""))
        val savingsGoal = BigDecimal(currentItem.savingsGoalAmount.replace(",",""))

        if(savingsGoal.compareTo(savingsEarned)!! <=0) {
            holder.ivCompletedCheck.visibility = View.VISIBLE
        }

    }

    override fun getItemCount(): Int {
        return savingsList.size
    }

    class MyViewHolder(itemView: View, listener : onItemClickListener) : RecyclerView.ViewHolder(itemView){
        val tvSavingsTitle: TextView = itemView.findViewById(R.id.tvSavingsTitle)
        val tvSavingsAmount: TextView = itemView.findViewById(R.id.tvSavingsAmount)
        val tvGoalAmount: TextView = itemView.findViewById(R.id.tvGoalAmountRV)
        val ivCompletedCheck: ImageView = itemView.findViewById(R.id.ivCompletedCheck)


        init {
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }

    }


}