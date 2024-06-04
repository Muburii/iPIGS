package com.example.zufffinalyear.fragments

import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import android.widget.ArrayAdapter
import com.example.zufffinalyear.R
import com.example.zufffinalyear.databinding.FragmentAddStallDialogueBinding
import com.example.zufffinalyear.models.AutocompleteValidator
import com.example.zufffinalyear.models.Stalldetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class AddStallDialogue : DialogFragment() {

    private lateinit var binding: FragmentAddStallDialogueBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddStallDialogueBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

        val piggroupList = resources.getStringArray(R.array.pig_group_list)
        val piggroupAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, piggroupList)
        binding.piggroupTextView.setAdapter(piggroupAdapter)

        binding.piggroupTextView.setValidator(AutocompleteValidator(piggroupList))

        binding.StallNoEditText.filters = arrayOf(InputFilter.LengthFilter(2), InputFilter { source, start, end, _, _, _ ->
            for (i in start until end) {
                if (!Character.isDigit(source[i])) {
                    binding.StallNo.error = "Only integers are allowed"
                    return@InputFilter ""
                }
            }
            binding.StallNo.error = null
            null
        })

        binding.buttonConfirm.setOnClickListener {
            val selectedText = binding.piggroupTextView.text.toString()
            if (!AutocompleteValidator(piggroupList).isValid(selectedText)) {
                binding.piggroupTextInputLayout.error = "Please select a valid Pig Group"
                return@setOnClickListener
            } else {
                binding.piggroupTextInputLayout.error = null
            }
            val stallNo = binding.StallNoEditText.text.toString()
            if (stallNo.length != 2) {
                binding.StallNo.error = "Stall No must be exactly 2 digits"
                return@setOnClickListener
            } else {
                binding.StallNo.error = null
            }
            val pigGroup = selectedText

            val stall = Stalldetails(pigGroup = pigGroup, stallNo = stallNo)
            saveStallToRealtimeDatabase(stall)

            dismiss()
        }

        binding.buttonCancel.setOnClickListener {
            dismiss()
        }
    }

    private fun saveStallToRealtimeDatabase(stall: Stalldetails) {
        val user = auth.currentUser
        val userId = user?.uid

        if (userId != null) {
            val databaseReference = database.getReference("users").child(userId).child("stalls").child(stall.stallNo)
            databaseReference.setValue(stall)
                .addOnSuccessListener {
                    // Successfully added stall
                }
                .addOnFailureListener { e ->
                    binding.StallNo.error = "Failed to save data: ${e.message}"
                }
        }
    }
}
