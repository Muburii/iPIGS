package com.example.zufffinalyear.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zufffinalyear.adapter.IncomeAdapter
import com.example.zufffinalyear.databinding.FragmentIncomeBinding
import com.example.zufffinalyear.models.Incomeitem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class IncomeFragment : Fragment() {

    private var _binding: FragmentIncomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var incomeAdapter: IncomeAdapter
    private val incomeList = mutableListOf<Incomeitem>()
    private var totalIncome: Double = 0.0

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
        fetchIncomeData()
    }

    private fun fetchIncomeData() {
        val user = auth.currentUser
        val userId = user?.uid

        if (userId != null) {
            firestore.collection("users").document(userId).collection("income")
                .get()
                .addOnSuccessListener { result ->
                    incomeList.clear()
                    totalIncome = 0.0
                    for (document in result) {
                        document.reference.collection("incomeitems").get()
                            .addOnSuccessListener { items ->
                                processIncomeItems(items)
                            }
                            .addOnFailureListener { e ->
                                // Handle any errors
                                e.printStackTrace()
                            }
                    }
                }
                .addOnFailureListener { e ->
                    // Handle any errors
                    e.printStackTrace()
                }
        }
    }

    private fun processIncomeItems(items: QuerySnapshot) {
        for (item in items) {
            val description = item.getString("description") ?: ""
            val amount = item.getDouble("amount") ?: 0.0
            val date = item.getString("date") ?: ""
            val tagNo = item.getString("tagNo") ?: ""
            val incomeItem = Incomeitem(description, tagNo, amount, date)
            incomeList.add(incomeItem)
            totalIncome += amount
        }
        incomeAdapter.notifyDataSetChanged()
        updateTotalIncome()
    }

    private fun updateTotalIncome() {
        binding.totalIncomeTextView.text = String.format("%.2f", totalIncome)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
