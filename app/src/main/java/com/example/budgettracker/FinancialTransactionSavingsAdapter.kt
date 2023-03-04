package com.example.budgettracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FinancialTransactionSavingsAdapter(
    private val financialTransactionsSavingsList: ArrayList<FinancialTransactionSavingsModel>):
    RecyclerView.Adapter<FinancialTransactionSavingsAdapter.FtsViewHolder>() {

    private lateinit var mListener : onItemClickListener

    interface onItemClickListener{
        fun onItemClick(position : Int)
    }

    fun setOnItemClickListener(listener: onItemClickListener){
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FtsViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.list_savings_ft_items,
            parent,
            false
        )
        return FtsViewHolder(itemView,mListener)
    }

    override fun onBindViewHolder(holder: FtsViewHolder, position: Int) {
        val currentItem = financialTransactionsSavingsList[position]
        holder.ftsName.text = currentItem.ftsName
        holder.ftsAmount.text = BigDecimalTools.prepareForPrint(currentItem.ftsAmount)
        holder.ftsTransacDate.text= CalendarTools.getCalendarFormatDDMMYYYY(
            currentItem.ftsTransacDate
        )
    }

    override fun getItemCount(): Int {
        return financialTransactionsSavingsList.size;
    }

    class FtsViewHolder(itemView: View, listener: onItemClickListener):
        RecyclerView.ViewHolder(itemView){
        val ftsName : TextView = itemView.findViewById(R.id.tvFtsName)
        val ftsAmount : TextView = itemView.findViewById(R.id.tvFtsAmount)
        val ftsTransacDate : TextView = itemView.findViewById(R.id.tvFtsTransacDate)

        init{
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
    }


}