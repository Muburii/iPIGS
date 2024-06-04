package com.example.zufffinalyear.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.zufffinalyear.databinding.IncomeitemBinding
import com.example.zufffinalyear.models.Incomeitem

class IncomeAdapter(private val incomeList: MutableList<Incomeitem>) : RecyclerView.Adapter<IncomeAdapter.IncomeViewHolder>() {

    inner class IncomeViewHolder(private val binding: IncomeitemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(incomeItem: Incomeitem) {
            binding.apply {
                descriptionTextView.text = incomeItem.description
                amountTextView.text = incomeItem.amount.toString()
                dateTextView.text = incomeItem.date
                tagNoTextView.text = incomeItem.tagNo
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IncomeViewHolder {
        val binding = IncomeitemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return IncomeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: IncomeViewHolder, position: Int) {
        holder.bind(incomeList[position])
    }

    override fun getItemCount(): Int {
        return incomeList.size
    }

    fun addIncomeItem(incomeItem: Incomeitem) {
        incomeList.add(incomeItem)
        notifyItemInserted(incomeList.size - 1)
    }
}
