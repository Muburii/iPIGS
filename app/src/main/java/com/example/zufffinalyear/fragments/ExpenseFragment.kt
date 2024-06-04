package com.example.zufffinalyear.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zufffinalyear.R
import com.example.zufffinalyear.adapter.ExpenseAdapter
import com.example.zufffinalyear.models.Expenseitem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ExpenseFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var recyclerView: RecyclerView
    private lateinit var expenseAdapter: ExpenseAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_expense, container, false)
        recyclerView = view.findViewById(R.id.recyclerViewExpense)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        setupRecyclerView()
        fetchExpenseItems()
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
                    if (snapshot != null && !snapshot.isEmpty) {
                        for (document in snapshot.documents) {
                            val expenseItem = document.toObject(Expenseitem::class.java)
                            expenseItem?.let { expenseList.add(it) }
                        }
                    }

                    expenseAdapter.updateExpenseList(expenseList)
                }
        }
    }
}
