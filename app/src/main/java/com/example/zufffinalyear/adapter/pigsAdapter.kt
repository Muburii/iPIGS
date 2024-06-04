package com.example.zufffinalyear.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.zufffinalyear.databinding.PigitemBinding
import com.example.zufffinalyear.models.Pigdetails
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class pigsAdapter(
    private var pigList: MutableList<Pigdetails>,
    private val onSellClickListener: (Pigdetails) -> Unit,
    private val onArchiveClickListener: (Pigdetails) -> Unit,
    private val onItemClickListener: (Pigdetails) -> Unit
) : RecyclerView.Adapter<pigsAdapter.PigViewHolder>() {

    private var filteredPigList: MutableList<Pigdetails> = pigList.toMutableList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PigViewHolder {
        val binding = PigitemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PigViewHolder(binding, onSellClickListener, onArchiveClickListener, onItemClickListener)
    }

    override fun onBindViewHolder(holder: PigViewHolder, position: Int) {
        val pig = filteredPigList[position]
        holder.bind(pig)
    }

    override fun getItemCount(): Int {
        return filteredPigList.size
    }

    fun filter(breed: String, group: String) {
        filteredPigList = pigList.filter {
            (breed.isEmpty() || breed == "All" || it.pigbreed.equals(breed, ignoreCase = true)) &&
                    (group.isEmpty() || group == "All" || it.piggroup.equals(group, ignoreCase = true))
        }.toMutableList()
        notifyDataSetChanged()
    }

    fun showAll() {
        filteredPigList = pigList.toMutableList()
        notifyDataSetChanged()
    }

    fun removePig(pig: Pigdetails) {
        pigList.remove(pig)
        filteredPigList.remove(pig)
        notifyDataSetChanged()
    }

    class PigViewHolder(
        private val binding: PigitemBinding,
        private val onSellClickListener: (Pigdetails) -> Unit,
        private val onArchiveClickListener: (Pigdetails) -> Unit,
        private val onItemClickListener: (Pigdetails) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(pig: Pigdetails) {
            binding.apply {
                PigBreedTextView.text = pig.pigbreed
                tagNoTextView.text = pig.tag_no
                ageTextView.text = calculateAge(pig.dateofbirth)

                buttonSell.setOnClickListener {
                    onSellClickListener(pig)
                }

                buttonArchive.setOnClickListener {
                    onArchiveClickListener(pig)
                }

                itemView.setOnClickListener {
                    onItemClickListener(pig)
                }
            }
        }

        @SuppressLint("SimpleDateFormat")
        private fun calculateAge(dateOfBirth: String): String {
            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.US)
            val birthDate = Calendar.getInstance().apply {
                time = formatter.parse(dateOfBirth)!!
            }
            val currentDate = Calendar.getInstance()
            val years = currentDate.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR)
            val months = currentDate.get(Calendar.MONTH) - birthDate.get(Calendar.MONTH)
            val days = currentDate.get(Calendar.DAY_OF_MONTH) - birthDate.get(Calendar.DAY_OF_MONTH)

            val totalDays = years * 365 + months * 30 + days
            val weeks = totalDays / 7
            val remainingDays = totalDays % 7

            return "$weeks weeks, $remainingDays days"
        }
    }
}
