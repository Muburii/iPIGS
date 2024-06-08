package com.example.zufffinalyear.fragments

import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.zufffinalyear.databinding.FragmentDialogueBinding

class DialogueFragment(private val onConfirm: (Double) -> Unit) : DialogFragment() {

    private var _binding: FragmentDialogueBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDialogueBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.priceEditText.filters = arrayOf(InputFilter { source, start, end, _, _, _ ->
            // Reject any input that's not a digit
            for (i in start until end) {
                if (!Character.isDigit(source[i])) {
                    showToast("Only integers are allowed")
                    return@InputFilter ""
                }
            }
            null // Accept the input
        })

        binding.buttonConfirm.setOnClickListener {
            val price = binding.priceEditText.text.toString().toDoubleOrNull()
            if (price != null) {
                onConfirm(price)
                dismiss()
            } else {
                showToast("Please enter a valid price")
            }
        }

        binding.buttonCancel.setOnClickListener {
            dismiss()
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
