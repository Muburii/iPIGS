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
import com.example.zufffinalyear.adapter.IncomeAdapter
import com.example.zufffinalyear.models.Incomeitem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SalesFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var recyclerView: RecyclerView
    private lateinit var incomeAdapter: IncomeAdapter
    private lateinit var totalIncomeTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_sales, container, false)
        recyclerView = view.findViewById(R.id.recyclerViewIncome)
        totalIncomeTextView = view.findViewById(R.id.totalIncomeTextView)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        setupRecyclerView()
        fetchIncomeItems()
    }

    private fun setupRecyclerView() {
        incomeAdapter = IncomeAdapter(mutableListOf())
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = incomeAdapter
        }
    }

    private fun fetchIncomeItems() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            firestore.collection("users").document(userId).collection("income")
                .addSnapshotListener { snapshot, exception ->
                    if (exception != null) {
                        // Handle exception
                        return@addSnapshotListener
                    }

                    val incomeList = mutableListOf<Incomeitem>()
                    var totalIncome = 0.0
                    if (snapshot != null && !snapshot.isEmpty) {
                        for (document in snapshot.documents) {
                            val incomeItem = document.toObject(Incomeitem::class.java)
                            incomeItem?.let {
                                incomeList.add(it)
                                totalIncome += it.amount
                            }
                        }
                    }

                    incomeAdapter.updateIncomeList(incomeList)
                    totalIncomeTextView.text = totalIncome.toString()
                }
        }
    }

    fun addIncomeItem(incomeItem: Incomeitem) {
        incomeAdapter.addIncomeItem(incomeItem)
        val currentTotalIncome = totalIncomeTextView.text.toString().toDouble()
        val newTotalIncome = currentTotalIncome + incomeItem.amount
        totalIncomeTextView.text = newTotalIncome.toString()
    }
}
