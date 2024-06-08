package com.example.zufffinalyear.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.zufffinalyear.R
import com.example.zufffinalyear.databinding.FragmentHomeBinding
import com.example.zufffinalyear.models.Pigdetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

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

        // Initialize Firestore and FirebaseAuth
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Fetch and display pig breed counts
        fetchPigBreedCounts()
    }

    private fun fetchPigBreedCounts() {
        val user = auth.currentUser
        val userId = user?.uid

        if (userId != null) {
            firestore.collection("users").document(userId).collection("pigs")
                .get()
                .addOnSuccessListener { querySnapshot ->
                    val breedCounts = processBreedCounts(querySnapshot)
                    displayBreedCounts(breedCounts)
                }
                .addOnFailureListener { e ->
                    // Handle failure
                }
        }
    }

    private fun processBreedCounts(querySnapshot: QuerySnapshot): Map<String, Int> {
        val breedCounts = mutableMapOf<String, Int>()
        for (document in querySnapshot) {
            val pig = document.toObject(Pigdetails::class.java)
            val breed = pig.pigbreed
            breedCounts[breed] = breedCounts.getOrDefault(breed, 0) + 1
        }
        return breedCounts
    }

    private fun displayBreedCounts(breedCounts: Map<String, Int>) {
        val breedCountLayout = binding.breedCountLayout
        breedCountLayout.removeAllViews()

        for ((breed, count) in breedCounts) {
            val textView = TextView(requireContext()).apply {
                text = getString(R.string.breed_count, breed, count)
                textSize = 18f
                setPadding(8, 5, 8, 5)
                setTextColor(resources.getColor(android.R.color.black, null))
                setTypeface(null, android.graphics.Typeface.BOLD)
            }
            breedCountLayout.addView(textView)
        }
    }
}
