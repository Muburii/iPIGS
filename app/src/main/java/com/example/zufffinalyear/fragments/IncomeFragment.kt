package com.example.zufffinalyear.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zufffinalyear.adapter.IncomeAdapter
import com.example.zufffinalyear.databinding.FragmentIncomeBinding
import com.example.zufffinalyear.models.Incomeitem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class IncomeFragment : Fragment() {

    private var _binding: FragmentIncomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var incomeAdapter: IncomeAdapter
    private val incomeList = mutableListOf<Incomeitem>()
    private var totalIncome: Float = 0f
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentIncomeBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        incomeAdapter = IncomeAdapter(incomeList)
        binding.recyclerViewIncome.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = incomeAdapter
        }
        setFragmentResultListener("sellResult") { _, bundle ->
            val description = bundle.getString("description", "")
            val amount = bundle.getFloat("amount")
            val date = bundle.getString("date", "")
            val tagNo = bundle.getString("tagNo", "")
            addIncomeItem(description, amount, date, tagNo)
        }
    }
    override fun onResume() {
        super.onResume()
        fetchIncomeData() // Fetch data when the fragment is visible
    }
    private fun fetchIncomeData() {
        val user = auth.currentUser
        val userId = user?.uid

        if (userId != null) {
            firestore.collection("users").document(userId).collection("income")
                .get()
                .addOnSuccessListener { result ->
                    incomeList.clear()
                    totalIncome = 0f
                    for (document in result) {
                        val description = document.getString("description") ?: ""
                        val amount = document.getDouble("amount")?.toFloat() ?: 0f
                        val date = document.getString("date") ?: ""
                        val tagNo = document.getString("tagNo") ?: ""
                        val incomeItem = Incomeitem(description, tagNo, amount, date)
                        incomeList.add(incomeItem)
                        totalIncome += amount
                    }
                    incomeAdapter.notifyDataSetChanged()
                    updateTotalIncome()
                }
                .addOnFailureListener {
                    // Handle any errors
                }
        }
    }
    @SuppressLint("DefaultLocale")
    private fun updateTotalIncome() {
        binding.totalIncomeTextView.text = String.format("%.2f", totalIncome)
    }
    private fun addIncomeItem(description: String, amount: Float, date: String, tagNo: String) {
        val newItem = Incomeitem(description, tagNo, amount, date)
        incomeAdapter.addIncomeItem(newItem)
        totalIncome += amount
        updateTotalIncome()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
