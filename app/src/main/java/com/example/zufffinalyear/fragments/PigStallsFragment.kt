package com.example.zufffinalyear.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zufffinalyear.adapters.StallAdapter
import com.example.zufffinalyear.databinding.FragmentPigStallsBinding
import com.example.zufffinalyear.models.Incomeitem
import com.example.zufffinalyear.models.Stalldetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PigStallsFragment : Fragment() {

    private lateinit var binding: FragmentPigStallsBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private lateinit var stallAdapter: StallAdapter
    private var stalls = mutableListOf<Stalldetails>()
    private var stallsListener: ValueEventListener? = null
    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPigStallsBinding.inflate(inflater, container, false)

        binding.fabAddStall.setOnClickListener {
            val dialogFragment = AddStallDialogue()
            dialogFragment.show(parentFragmentManager, "AddStallDialogue")
        }

        stallAdapter = StallAdapter(
            stalls,
            onItemClicked = { stall -> onStallSelected(stall) },
            onSellClicked = { stall -> onSellClicked(stall) }
        )
        binding.rvPigStalls.adapter = stallAdapter
        binding.rvPigStalls.layoutManager = LinearLayoutManager(context)

        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        fetchStallsFromRealtimeDatabase()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stallsListener?.let { database.reference.removeEventListener(it) }
    }

    private fun fetchStallsFromRealtimeDatabase() {
        val user = auth.currentUser
        val userId = user?.uid

        if (userId != null) {
            val stallsReference = database.getReference("users").child(userId).child("stalls")
            stallsListener = stallsReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    stalls.clear()
                    for (snapshot in dataSnapshot.children) {
                        val stall = snapshot.getValue(Stalldetails::class.java)
                        if (stall != null) {
                            stalls.add(stall)
                        }
                    }
                    stallAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle possible errors.
                }
            })
        }
    }

    private fun onStallSelected(stall: Stalldetails) {
        val action = PigStallsFragmentDirections.actionPigStallsFragmentToPigsFragment(stall.stallNo)
        findNavController().navigate(action)
    }

    private fun onSellClicked(stall: Stalldetails) {
        val dialogFragment = DialogueFragment { price ->
            val totalIncome = price * stall.numberOfPigs
            sellPigs(stall, totalIncome)
        }
        dialogFragment.show(parentFragmentManager, "SellPigsDialog")
    }

    private fun sellPigs(stall: Stalldetails, totalIncome: Double) {
        val user = auth.currentUser
        val userId = user?.uid

        if (userId != null) {
            val currentDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
            val incomeItem = Incomeitem(
                description = "Selling all pigs in stall ${stall.stallNo}",
                amount = totalIncome,
                date = currentDate
            )

            firestore.collection("users").document(userId)
                .collection("income").add(incomeItem)
                .addOnSuccessListener {
                    removePigsFromStall(userId, stall.stallNo)
                    Toast.makeText(requireContext(), "Sold pigs for $totalIncome", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Failed to record sale: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun removePigsFromStall(userId: String, stallNo: String) {
        val stallReference = database.getReference("users").child(userId).child("stalls").child(stallNo)
        stallReference.child("pigs").removeValue().addOnSuccessListener {
            stallReference.child("numberOfPigs").setValue(0)
            stallReference.child("meanWeight").setValue(0.0)
        }.addOnFailureListener { e ->
            Toast.makeText(requireContext(), "Failed to remove pigs: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
