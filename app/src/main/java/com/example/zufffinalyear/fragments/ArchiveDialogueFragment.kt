package com.example.zufffinalyear.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.zufffinalyear.R
import com.example.zufffinalyear.databinding.FragmentArchiveDialogueBinding
import com.example.zufffinalyear.models.Pigdetails
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import java.text.SimpleDateFormat
import java.util.*

class ArchiveDialogueFragment(
    private val pig: Pigdetails,
    private val onArchive: (String, String, Float, String) -> Unit
) : DialogFragment() {

    private var _binding: FragmentArchiveDialogueBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentArchiveDialogueBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val reasons = resources.getStringArray(R.array.Reason_for_archiving_list)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, reasons)
        (binding.reasonforarchivingTextInputLayout.editText as? MaterialAutoCompleteTextView)?.setAdapter(adapter)

        val calendar = Calendar.getInstance()
        val dateFormatter = SimpleDateFormat("dd-MM-yyyy", Locale.US)

        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            binding.dateofbirthEditText.setText(dateFormatter.format(calendar.time))
        }

        binding.dateofbirthEditText.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        binding.priceEditText.filters = arrayOf(InputFilter { source, start, end, _, _, _ ->
            for (i in start until end) {
                if (!Character.isDigit(source[i])) {
                    showToast("Only integers are allowed")
                    return@InputFilter ""
                }
            }
            null
        })

        binding.buttonConfirm.setOnClickListener {
            val reason = binding.reasonforarchivingTextView.text.toString()
            val date = binding.dateofbirthEditText.text.toString()
            val price = binding.priceEditText.text.toString().toFloatOrNull() ?: 0f
            val notes = binding.ArchivingNotesEditText.text.toString()

            if (reason.isNotBlank() && date.isNotBlank() && price > 0) {
                onArchive(reason, date, price, notes)
                dismiss()
            } else {
                showToast("Please fill in all required fields.")
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
