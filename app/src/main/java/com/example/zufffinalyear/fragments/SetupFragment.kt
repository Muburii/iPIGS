package com.example.zufffinalyear.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.zufffinalyear.R
import com.example.zufffinalyear.databinding.FragmentSetupBinding
import com.example.zufffinalyear.models.Expenseitem
import com.example.zufffinalyear.models.Incomeitem
import com.example.zufffinalyear.utils.DateValueFormatter
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class SetupFragment : Fragment() {

    private var _binding: FragmentSetupBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSetupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        fetchAndPlotData()
    }

    private fun fetchAndPlotData() {
        val userId = auth.currentUser?.uid

        if (userId != null) {
            firestore.collection("users").document(userId).collection("income")
                .get()
                .addOnSuccessListener { incomeSnapshot ->
                    if (incomeSnapshot != null && !incomeSnapshot.isEmpty) {
                        val incomeByDay = mutableMapOf<Int, Double>()
                        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

                        for (document in incomeSnapshot.documents) {
                            val incomeItem = document.toObject(Incomeitem::class.java)
                            incomeItem?.let {
                                val date = dateFormat.parse(it.date)
                                val calendar = Calendar.getInstance()
                                calendar.time = date!!
                                val day = calendar.get(Calendar.DAY_OF_MONTH)
                                incomeByDay[day] = incomeByDay.getOrDefault(day, 0.0) + it.amount
                            }
                        }

                        firestore.collection("users").document(userId).collection("expenses")
                            .get()
                            .addOnSuccessListener { expenseSnapshot ->
                                if (expenseSnapshot != null && !expenseSnapshot.isEmpty) {
                                    val expenseByDay = mutableMapOf<Int, Double>()

                                    for (document in expenseSnapshot.documents) {
                                        val expenseItem = document.toObject(Expenseitem::class.java)
                                        expenseItem?.let {
                                            val date = dateFormat.parse(it.date)
                                            val calendar = Calendar.getInstance()
                                            calendar.time = date!!
                                            val day = calendar.get(Calendar.DAY_OF_MONTH)
                                            expenseByDay[day] = expenseByDay.getOrDefault(day, 0.0) + it.amount
                                        }
                                    }

                                    plotIncomeBarChart(incomeByDay)
                                    plotExpenseBarChart(expenseByDay)
                                    plotIncomeExpensePieChart(incomeByDay, expenseByDay)
                                }
                            }
                    }
                }
        }
    }

    private fun plotIncomeBarChart(incomeByDay: Map<Int, Double>) {
        val entries = mutableListOf<BarEntry>()
        for ((day, totalIncome) in incomeByDay) {
            entries.add(BarEntry(day.toFloat(), totalIncome.toFloat()))
        }

        val dataSet = BarDataSet(entries, "Total Income by Day")
        dataSet.colors = listOf(ContextCompat.getColor(requireContext(), R.color.green))
        dataSet.valueTextSize = 10f

        val barData = BarData(dataSet)
        barData.barWidth = 0.3f // Set the bar width to a uniform size

        binding.barChart.data = barData
        binding.barChart.description.isEnabled = false

        // Configure the X-axis
        val xAxis = binding.barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.valueFormatter = DateValueFormatter()

        // Configure the left Y-axis
        val leftAxis = binding.barChart.axisLeft
        leftAxis.setDrawGridLines(false)

        // Disable the right Y-axis
        binding.barChart.axisRight.isEnabled = false

        // Refresh the chart
        binding.barChart.invalidate()
    }

    private fun plotExpenseBarChart(expenseByDay: Map<Int, Double>) {
        val entries = mutableListOf<BarEntry>()
        for ((day, totalExpense) in expenseByDay) {
            entries.add(BarEntry(day.toFloat(), totalExpense.toFloat()))
        }

        val dataSet = BarDataSet(entries, "Total Expense by Day")
        dataSet.colors = listOf(ContextCompat.getColor(requireContext(), R.color.yellow))
        dataSet.valueTextSize = 10f

        val barData = BarData(dataSet)
        barData.barWidth = 0.3f // Set the bar width to a uniform size

        binding.expenseBarChart.data = barData
        binding.expenseBarChart.description.isEnabled = false

        // Configure the X-axis
        val xAxis = binding.expenseBarChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.valueFormatter = DateValueFormatter()

        // Configure the left Y-axis
        val leftAxis = binding.expenseBarChart.axisLeft
        leftAxis.setDrawGridLines(false)

        // Disable the right Y-axis
        binding.expenseBarChart.axisRight.isEnabled = false

        // Refresh the chart
        binding.expenseBarChart.invalidate()
    }

    private fun plotIncomeExpensePieChart(incomeByDay: Map<Int, Double>, expenseByDay: Map<Int, Double>) {
        val totalIncome = incomeByDay.values.sum()
        val totalExpense = expenseByDay.values.sum()

        val entries = mutableListOf<PieEntry>()
        entries.add(PieEntry(totalIncome.toFloat(), "Income"))
        entries.add(PieEntry(totalExpense.toFloat(), "Expenses"))

        val dataSet = PieDataSet(entries, "Income vs Expenses")
        dataSet.colors = listOf(
            ContextCompat.getColor(requireContext(), R.color.green),
            ContextCompat.getColor(requireContext(), R.color.yellow)
        )
        dataSet.valueTextSize = 10f

        val pieData = PieData(dataSet)
        binding.pieChart.data = pieData
        binding.pieChart.description.isEnabled = false
        binding.pieChart.invalidate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
