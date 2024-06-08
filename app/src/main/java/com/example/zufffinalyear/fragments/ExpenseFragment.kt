package com.example.zufffinalyear.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zufffinalyear.R
import com.example.zufffinalyear.adapter.ExpenseAdapter
import com.example.zufffinalyear.models.Expenseitem
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ExpenseFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var recyclerView: RecyclerView
    private lateinit var expenseAdapter: ExpenseAdapter
    private lateinit var totalExpenseTextView: TextView
    private lateinit var barChart: BarChart

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_expense, container, false)
        recyclerView = view.findViewById(R.id.recyclerViewExpense)
        totalExpenseTextView = view.findViewById(R.id.totalExpenseTextView)
        barChart = view.findViewById(R.id.barChart)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        setupRecyclerView()
        fetchExpenseItems()
        setupBarChart()
    }

    private fun setupRecyclerView() {
        expenseAdapter = ExpenseAdapter(mutableListOf())
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = expenseAdapter
        }
    }

    private fun fetchExpenseItems() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            firestore.collection("users").document(userId).collection("expenses")
                .addSnapshotListener { snapshot, exception ->
                    if (exception != null) {
                        // Handle exception
                        return@addSnapshotListener
                    }

                    val expenseList = mutableListOf<Expenseitem>()
                    var totalExpense = 0.0
                    if (snapshot != null && !snapshot.isEmpty) {
                        for (document in snapshot.documents) {
                            val expenseItem = document.toObject(Expenseitem::class.java)
                            expenseItem?.let {
                                expenseList.add(it)
                                totalExpense += it.amount
                            }
                        }
                    }

                    expenseAdapter.updateExpenseList(expenseList)
                    totalExpenseTextView.text = totalExpense.toString()
                    updateBarChart(expenseList)
                }
        }
    }

    private fun setupBarChart() {
        barChart.description.isEnabled = false
        barChart.setTouchEnabled(true)
        barChart.setPinchZoom(true)

        // Remove grid lines but keep the axis lines
        barChart.xAxis.setDrawGridLines(false)
        barChart.axisLeft.setDrawGridLines(false)
        barChart.axisRight.setDrawGridLines(false)

        // Ensure axis lines are shown
        barChart.xAxis.setDrawAxisLine(true)
        barChart.axisLeft.setDrawAxisLine(true)
        barChart.axisRight.setDrawAxisLine(true)
    }

    private fun updateBarChart(expenseList: List<Expenseitem>) {
        val entries = expenseList.mapIndexed { index, expense ->
            BarEntry(index.toFloat(), expense.amount.toFloat())
        }
        val dataSet = BarDataSet(entries, "Expenses")
        val barData = BarData(dataSet)
        barChart.data = barData
        barChart.invalidate()
    }

    fun addExpenseItem(expenseItem: Expenseitem) {
        expenseAdapter.addExpenseItem(expenseItem)
        val currentTotalExpense = totalExpenseTextView.text.toString().toDouble()
        val newTotalExpense = currentTotalExpense + expenseItem.amount
        totalExpenseTextView.text = newTotalExpense.toString()
        updateBarChart(expenseAdapter.expenseList)
    }
}
