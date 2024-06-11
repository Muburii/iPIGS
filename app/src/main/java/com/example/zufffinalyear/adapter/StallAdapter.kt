package com.example.zufffinalyear.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.zufffinalyear.databinding.StallDetailsBinding
import com.example.zufffinalyear.models.Stalldetails

class StallAdapter(
    private val stalls: List<Stalldetails>,
    private val onItemClicked: (Stalldetails) -> Unit,
    private val onSellClicked: (Stalldetails) -> Unit
) : RecyclerView.Adapter<StallAdapter.StallViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StallViewHolder {
        val binding = StallDetailsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StallViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StallViewHolder, position: Int) {
        holder.bind(stalls[position])
    }

    override fun getItemCount(): Int = stalls.size

    inner class StallViewHolder(private val binding: StallDetailsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(stall: Stalldetails) {
            binding.piggrouptv.text = stall.pigGroup
            binding.StallNotv.text = stall.stallNo
            binding.noofpigsTextView.text = stall.numberOfPigs.toString()

            binding.root.setOnClickListener { onItemClicked(stall) }
            binding.buttonSell.setOnClickListener { onSellClicked(stall) }
        }
    }
}
