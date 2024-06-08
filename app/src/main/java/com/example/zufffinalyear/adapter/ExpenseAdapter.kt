package com.example.zufffinalyear.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.zufffinalyear.R
import com.example.zufffinalyear.models.Expenseitem

class ExpenseAdapter(val expenseList: MutableList<Expenseitem>) : RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    inner class ExpenseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val descriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)
        private val amountTextView: TextView = itemView.findViewById(R.id.amountTextView)
        private val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)

        fun bind(expenseItem: Expenseitem) {
            descriptionTextView.text = expenseItem.description
            amountTextView.text = expenseItem.amount.toString()
            dateTextView.text = expenseItem.date
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.expenseitem, parent, false)
        return ExpenseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        holder.bind(expenseList[position])
    }

    override fun getItemCount(): Int {
        return expenseList.size
    }

    fun updateExpenseList(newExpenseList: List<Expenseitem>) {
        expenseList.clear()
        expenseList.addAll(newExpenseList)
        notifyDataSetChanged()
    }

    fun addExpenseItem(expenseItem: Expenseitem) {
        expenseList.add(expenseItem)
        notifyItemInserted(expenseList.size - 1)
    }
}
