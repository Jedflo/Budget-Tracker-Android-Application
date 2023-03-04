package com.example.budgettracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FinancialTransactionDebtAdapter(
    private val financialTransactionDebtList: ArrayList<FinancialTransactionDebtModel>):
    RecyclerView.Adapter<FinancialTransactionDebtAdapter.FtdViewHolder>(){

    private lateinit var mListener : onItemClickListener

    interface onItemClickListener{
        fun onItemClick(position : Int)
    }

    fun setOnItemClickListener(listener: onItemClickListener){
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FtdViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.list_debt_ft_items,
            parent,
            false
        )
        return FtdViewHolder(itemView, mListener)
    }

    override fun onBindViewHolder(holder: FtdViewHolder, position: Int) {
        val currentItem = financialTransactionDebtList[position]
        holder.ftdName.text = currentItem.ftdName
        holder.ftdAmount.text = BigDecimalTools.prepareForPrint(currentItem.ftdAmount)
        holder.ftdTransacDate.text= CalendarTools.getCalendarFormatDDMMYYYY(
            currentItem.ftdTransacDate
        )
    }

    override fun getItemCount(): Int {
        return financialTransactionDebtList.size
    }

    class FtdViewHolder(itemView: View, listener: onItemClickListener):
        RecyclerView.ViewHolder(itemView) {
        val ftdName: TextView = itemView.findViewById(R.id.tvFtdName)
        val ftdAmount: TextView = itemView.findViewById(R.id.tvFtdAmount)
        val ftdTransacDate: TextView = itemView.findViewById(R.id.tvFtdTransactionDate)

        init{
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
    }

}