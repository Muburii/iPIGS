package com.example.zufffinalyear.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zufffinalyear.adapters.StallAdapter
import com.example.zufffinalyear.databinding.FragmentPigStallsBinding
import com.example.zufffinalyear.models.Stalldetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class PigStallsFragment : Fragment() {

    private lateinit var binding: FragmentPigStallsBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private lateinit var stallAdapter: StallAdapter
    private var stalls = mutableListOf<Stalldetails>()
    private var stallsListener: ValueEventListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPigStallsBinding.inflate(inflater, container, false)

        binding.fabAddStall.setOnClickListener {
            val dialogFragment = AddStallDialogue()
            dialogFragment.show(parentFragmentManager, "AddStallDialogue")
        }

        stallAdapter = StallAdapter(stalls) { stall -> onStallSelected(stall) }
        binding.rvPigStalls.adapter = stallAdapter
        binding.rvPigStalls.layoutManager = LinearLayoutManager(context)

        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

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
        val action = PigStallsFragmentDirections.actionPigStallsFragmentToPigsFragment()
        findNavController().navigate(action)
    }
}
