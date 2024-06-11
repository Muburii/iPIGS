package com.example.zufffinalyear.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.zufffinalyear.R
import com.example.zufffinalyear.models.Incomeitem

class IncomeAdapter(val incomeList: MutableList<Incomeitem>) : RecyclerView.Adapter<IncomeAdapter.IncomeViewHolder>() {

    inner class IncomeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val descriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)
        private val amountTextView: TextView = itemView.findViewById(R.id.amountTextView)
        private val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)

        fun bind(incomeItem: Incomeitem) {
            descriptionTextView.text = incomeItem.description
            amountTextView.text = incomeItem.amount.toString()
            dateTextView.text = incomeItem.date
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IncomeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.incomeitem, parent, false)
        return IncomeViewHolder(view)
    }

    override fun onBindViewHolder(holder: IncomeViewHolder, position: Int) {
        holder.bind(incomeList[position])
    }

    override fun getItemCount(): Int {
        return incomeList.size
    }

    fun updateIncomeList(newIncomeList: List<Incomeitem>) {
        incomeList.clear()
        incomeList.addAll(newIncomeList)
        notifyDataSetChanged()
    }

    fun addIncomeItem(incomeItem: Incomeitem) {
        incomeList.add(incomeItem)
        notifyItemInserted(incomeList.size - 1)
    }
}
