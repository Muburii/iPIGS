package com.example.zufffinalyear.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.example.zufffinalyear.R
import com.example.zufffinalyear.databinding.FragmentAddpigBinding
import java.util.Calendar

class AddpigFragment : Fragment() {

    private lateinit var binding: FragmentAddpigBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddpigBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Initialize ArrayAdapter
        val pigBreeds = resources.getStringArray(R.array.pig_breeds_list)
        val pigBreedsAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, pigBreeds)
        binding.autoCompleteTextView.setAdapter(pigBreedsAdapter)

        val genderList = resources.getStringArray(R.array.gender_list)
        val genderAdapter =ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, genderList)
        binding.autoCompletegenderTextView.setAdapter(genderAdapter)
        // DatePicker for Date of Birth
        binding.dateofbirthEditText.setOnClickListener {
            showDatePickerDialog { date ->
                binding.dateofbirthEditText.setText(date)
            }
        }
        // DatePicker for Date of Entry on the Farm
        binding.dateofentryEditText.setOnClickListener {
            showDatePickerDialog { date ->
                binding.dateofentryEditText.setText(date)
            }
        }
    }
    private fun showDatePickerDialog(onDateSet: (String) -> Unit) {
        val datePickerDialog = DatePickerDialog(requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDate =
                    getString(R.string.date_format, selectedDay, selectedMonth + 1, selectedYear)
                onDateSet(formattedDate)
            },
            Calendar.getInstance().get(Calendar.YEAR),
            Calendar.getInstance().get(Calendar.MONTH),
            Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }
}