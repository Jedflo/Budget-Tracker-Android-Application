package com.example.budgettracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FinancialTransactionWalletAdapter (
    private val financialTransactionsWalletList: ArrayList<FinancialTransactionWalletModel>):
    RecyclerView.Adapter<FinancialTransactionWalletAdapter.FtwViewHolder>() {

    private lateinit var mListener : onItemClickListener

    interface onItemClickListener{
        fun onItemClick(position : Int)
    }

    fun setOnItemClickListener(listener: onItemClickListener){
        mListener = listener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FtwViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.list_wallet_ft_items,
            parent,
            false
        )
        return FtwViewHolder(itemView,mListener)
    }

    override fun onBindViewHolder(holder: FtwViewHolder, position: Int) {
        val currentItem = financialTransactionsWalletList[position]
        holder.ftwName.text = currentItem.ftwName
        holder.ftwAmount.text = BigDecimalTools.prepareForPrint(currentItem.ftwAmount)
        holder.ftwTransacDate.text = CalendarTools.getCalendarFormatDDMMYYYY(
            currentItem.ftwTransactionDate
        )
    }

    override fun getItemCount(): Int {
        return financialTransactionsWalletList.size
    }



    class FtwViewHolder(itemView: View, listener: onItemClickListener):
        RecyclerView.ViewHolder(itemView){
        val ftwName: TextView = itemView.findViewById(R.id.tvFtwName)
        val ftwAmount: TextView = itemView.findViewById(R.id.tvFtwAmount)
        val ftwTransacDate: TextView = itemView.findViewById(R.id.tvFtwTransactionDate)

        init{
            itemView.setOnClickListener{
                listener.onItemClick(adapterPosition)
            }
        }

    }
}