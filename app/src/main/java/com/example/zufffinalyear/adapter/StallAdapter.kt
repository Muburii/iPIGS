package com.example.zufffinalyear.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.zufffinalyear.databinding.StallDetailsBinding
import com.example.zufffinalyear.models.Stalldetails
class StallAdapter(
    private val stalls: List<Stalldetails>,
    private val itemClickListener: (Stalldetails) -> Unit
) : RecyclerView.Adapter<StallAdapter.StallViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StallViewHolder {
        val binding = StallDetailsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StallViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StallViewHolder, position: Int) {
        val stall = stalls[position]
        holder.bind(stall, itemClickListener)
    }

    override fun getItemCount(): Int = stalls.size

    class StallViewHolder(private val binding: StallDetailsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(stall: Stalldetails, clickListener: (Stalldetails) -> Unit) {
            binding.StallNotv.text = stall.stallNo // Assuming you have a TextView with id stallNo in your StallDetailsBinding
            binding.piggrouptv.text = stall.pigGroup // Assuming you have a TextView with id pigGroup in your StallDetailsBinding
            binding.root.setOnClickListener { clickListener(stall) }
        }
    }
}
