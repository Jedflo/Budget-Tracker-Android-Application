package com.example.budgettracker.ui.dashboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.budgettracker.R

class WalletAdapter(private val walletList: ArrayList<WalletModel>) : RecyclerView.Adapter<WalletAdapter.MyViewHolder>() {

    private lateinit var mListener : onItemClickListener

    interface onItemClickListener{
        fun onItemClick(position : Int)
    }

    fun setOnItemClickListener(listener : onItemClickListener){

        mListener = listener

    }

    //Where the list_xxx_items.xml file will be linked.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_wallet_items, parent, false)
        return WalletAdapter.MyViewHolder(itemView, mListener)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = walletList[position]
        holder.walletName.setText(currentItem.walletName)
        holder.walletAmount.setText(currentItem.walletAmount)
    }

    override fun getItemCount(): Int {
        return walletList.size
    }

    class MyViewHolder(itemView: View, listener : WalletAdapter.onItemClickListener) : RecyclerView.ViewHolder(itemView){
        val walletName: TextView = itemView.findViewById(R.id.tvWalletName)
        val walletAmount: TextView = itemView.findViewById(R.id.tvWalletAmount)

        init {
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }

    }
}