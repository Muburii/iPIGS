package com.example.zufffinalyear.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.zufffinalyear.R
import com.example.zufffinalyear.databinding.FragmentHomeBinding
import com.example.zufffinalyear.models.Expenseitem
import com.example.zufffinalyear.models.Incomeitem
import com.example.zufffinalyear.models.Stalldetails
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize Firebase Auth and Database reference
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Fetch and display farm name
        fetchFarmName()
        // Fetch and display pie chart data
        fetchStallDataAndSetupChart()
        // Fetch and display income and expense data
        fetchIncomeAndExpenseData()

        // Set up click listener for the finance card
        binding.Financecard.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_setupFragment)
        }
    }

    private fun fetchFarmName() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            database.getReference("Users").child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        val farmName = dataSnapshot.child("farmname").value.toString()
                        binding.farmNameTextView.text = farmName
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle database read error
                }
            })
        }
    }

    private fun fetchStallDataAndSetupChart() {
        val user = auth.currentUser
        val userId = user?.uid

        if (userId != null) {
            val stallsReference = database.getReference("users").child(userId).child("stalls")
            stallsReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val entries = mutableListOf<PieEntry>()
                    for (snapshot in dataSnapshot.children) {
                        val stall = snapshot.getValue(Stalldetails::class.java)
                        if (stall != null) {
                            entries.add(PieEntry(stall.numberOfPigs.toFloat(), stall.stallNo))
                        }
                    }
                    setupPieChart(entries)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle possible errors.
                }
            })
        }
    }

    private fun fetchIncomeAndExpenseData() {
        val userId = auth.currentUser?.uid

        if (userId != null) {
            // Fetch income data
            firestore.collection("users").document(userId).collection("income")
                .addSnapshotListener { snapshot, exception ->
                    if (exception != null) {
                        // Handle exception
                        return@addSnapshotListener
                    }

                    var totalIncome = 0.0
                    if (snapshot != null && !snapshot.isEmpty) {
                        for (document in snapshot.documents) {
                            val incomeItem = document.toObject(Incomeitem::class.java)
                            incomeItem?.let {
                                totalIncome += it.amount
                            }
                        }
                    }

                    binding.totalIncomeTextView.text = "Total Sales $totalIncome"
                    calculateAndDisplayProfitOrLoss()
                }

            // Fetch expense data
            firestore.collection("users").document(userId).collection("expenses")
                .addSnapshotListener { snapshot, exception ->
                    if (exception != null) {
                        // Handle exception
                        return@addSnapshotListener
                    }

                    var totalExpense = 0.0
                    if (snapshot != null && !snapshot.isEmpty) {
                        for (document in snapshot.documents) {
                            val expenseItem = document.toObject(Expenseitem::class.java)
                            expenseItem?.let {
                                totalExpense += it.amount
                            }
                        }
                    }

                    binding.totalExpenseTextView.text = "Total Expense: $totalExpense"
                    calculateAndDisplayProfitOrLoss()
                }
        }
    }

    private fun calculateAndDisplayProfitOrLoss() {
        val totalIncome = binding.totalIncomeTextView.text.toString().replace("Total Income: ", "").toDoubleOrNull() ?: 0.0
        val totalExpense = binding.totalExpenseTextView.text.toString().replace("Total Expense: ", "").toDoubleOrNull() ?: 0.0
        val profitOrLoss = totalIncome - totalExpense
        val profitOrLossText = if (profitOrLoss >= 0) {
            "Profit: $profitOrLoss"
        } else {
            "Loss: ${-profitOrLoss}"
        }
        binding.profitLossTextView.text = profitOrLossText
    }

    private fun setupPieChart(entries: List<PieEntry>) {
        val dataSet = PieDataSet(entries, "Pigs per Stall")
        dataSet.colors = listOf(
            ContextCompat.getColor(requireContext(), R.color.green),
            ContextCompat.getColor(requireContext(), R.color.yellow),
            ContextCompat.getColor(requireContext(), R.color.color2)
        ) // Customize colors as needed

        dataSet.valueTextSize = 10f // Change this value to adjust the font size of the labels

        val data = PieData(dataSet)
        binding.pieChart.data = data

        binding.pieChart.legend.textSize = 10f // Change this value to adjust the legend font size

        binding.pieChart.invalidate() // Refresh the chart
    }
}
