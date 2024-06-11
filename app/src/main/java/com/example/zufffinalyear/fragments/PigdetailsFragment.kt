package com.example.zufffinalyear.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.zufffinalyear.R
import com.example.zufffinalyear.databinding.FragmentPigdetailsBinding
import com.example.zufffinalyear.models.Pigdetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PigdetailsFragment : Fragment() {

    private lateinit var binding: FragmentPigdetailsBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private var stallId: String? = null
    private var documentId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPigdetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        stallId = arguments?.getString("stallId")
        documentId = arguments?.getString("documentId")

        fetchPigDetails()

        binding.updateButton.setOnClickListener {
            navigateToUpdateDetails()
        }
    }

    private fun fetchPigDetails() {
        val user = auth.currentUser
        val userId = user?.uid

        if (userId != null && stallId != null && documentId != null) {
            firestore.collection("users").document(userId).collection("stalls")
                .document(stallId!!).collection("pigs")
                .document(documentId!!)
                .get()
                .addOnSuccessListener { document ->
                    val pigDetails = document.toObject(Pigdetails::class.java)
                    if (pigDetails != null) {
                        displayPigDetails(pigDetails)
                    }
                }
                .addOnFailureListener { exception ->
                    // Handle any errors
                }
        }
    }

    private fun displayPigDetails(pigDetails: Pigdetails) {
        binding.apply {
            tagNotv.text = pigDetails.tag_no
            pigbreedtv.text = pigDetails.pigbreed
            gendertv.text = pigDetails.gender
            piggrouptv.text = pigDetails.piggroup
            litternotv.text = pigDetails.litter_no
            Weighttv.text = pigDetails.weight
            dateofbirthtv.text = pigDetails.dateofbirth
            dateofentrytv.text = pigDetails.dateofentryonfarm
            mothertagtv.text = pigDetails.motherstagno
            fatherstv.text = pigDetails.fatherstagno
        }
    }

    private fun navigateToUpdateDetails() {
        val bundle = Bundle().apply {
            putString("stallNo", stallId)
            putString("documentId", documentId)
        }
        findNavController().navigate(R.id.action_profileFragment_to_addpigFragment, bundle)
    }
}

