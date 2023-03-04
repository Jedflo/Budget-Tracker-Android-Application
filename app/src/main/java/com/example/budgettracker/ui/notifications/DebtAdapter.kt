package com.example.budgettracker.ui.notifications

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.budgettracker.R
import java.math.BigDecimal

class DebtAdapter(private val debtList: ArrayList<DebtModel>): RecyclerView.Adapter<DebtAdapter.MyViewHolder>() {

    private lateinit var mListener : onItemClickListener

    interface onItemClickListener{
        fun onItemClick(position : Int)
    }

    fun setOnItemClickListener(listener : onItemClickListener){

        mListener = listener

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.list_debt_items,
            parent,
            false
        )
        return MyViewHolder(itemView, mListener)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = debtList[position]
        holder.tvDebtTitle.setText(currentItem.DebtName)
        holder.tvDebtAmount.setText(currentItem.DebtAmount)
        holder.tvDebtPayments.setText(currentItem.DebtPayments)

        val payed = BigDecimal(currentItem.DebtPayments.replace(",",""))
        val debt = BigDecimal(currentItem.DebtAmount.replace(",",""))

        if(debt.compareTo(payed)<=0){
            holder.imDebtCompletedCheck.visibility = View.VISIBLE
        }

    }

    override fun getItemCount(): Int {
        return debtList.size
    }

    class MyViewHolder(itemView: View, listener : onItemClickListener): RecyclerView.ViewHolder(itemView) {
        val tvDebtTitle: TextView = itemView.findViewById(R.id.tvDebtName)
        val tvDebtAmount: TextView = itemView.findViewById(R.id.tvDebtAmount)
        val tvDebtPayments: TextView = itemView.findViewById(R.id.tvDebtPaymentTotal)
        val imDebtCompletedCheck: ImageView = itemView.findViewById(R.id.ivDebtCompletedCheck)

        init{
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }

    }


}