package com.example.zufffinalyear.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.zufffinalyear.R
import com.example.zufffinalyear.databinding.FragmentAddpigBinding
import com.example.zufffinalyear.models.AutocompleteValidator
import com.example.zufffinalyear.models.Pigdetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

class AddpigFragment : Fragment() {

    private lateinit var binding: FragmentAddpigBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private val args: AddpigFragmentArgs by navArgs()
    private var addedPigDocumentId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddpigBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        setupDropdowns()
        setupDatePickerDialogs()
        setupSaveButton()
    }

    private fun setupDropdowns() {
        // Set up pig breeds dropdown
        val pigBreeds = resources.getStringArray(R.array.pig_breeds_list)
        val pigBreedsAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, pigBreeds)
        binding.autoCompleteTextView.setAdapter(pigBreedsAdapter)
        binding.autoCompleteTextView.setValidator(AutocompleteValidator(pigBreeds))

        // Set up gender dropdown
        val genderList = resources.getStringArray(R.array.gender_list)
        val genderAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, genderList)
        binding.autoCompletegenderTextView.setAdapter(genderAdapter)
        binding.autoCompletegenderTextView.setValidator(AutocompleteValidator(genderList))

        val pigobtainedList = resources.getStringArray(R.array.pig_was_obtained_list)
        val pigobtainedAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, pigobtainedList)
        binding.autoCompletehowpigwasobtanedtextView.setAdapter(pigobtainedAdapter)
        binding.autoCompletehowpigwasobtanedtextView.setValidator(AutocompleteValidator(pigobtainedList))

        // Set up pig group dropdown
        val piggroupList = resources.getStringArray(R.array.pig_group_list)
        val piggroupAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, piggroupList)
        binding.autoCompletepiggroupTextView.setAdapter(piggroupAdapter)
        binding.autoCompletepiggroupTextView.setValidator(AutocompleteValidator(piggroupList))
    }

    private fun setupDatePickerDialogs() {
        binding.dateofbirthEditText.setOnClickListener {
            showDatePickerDialog { date ->
                binding.dateofbirthEditText.setText(date)
            }
        }
        binding.dateofentryEditText.setOnClickListener {
            showDatePickerDialog { date ->
                binding.dateofentryEditText.setText(date)
            }
        }
    }

    private fun setupSaveButton() {
        binding.saveButton.setOnClickListener {
            if (validateInputs()) {
                saveDataToFirestore()
            }
        }
    }

    private fun validateInputs(): Boolean {
        var isValid = true
        val dateOfBirth = binding.dateofbirthEditText.text.toString()
        val tagNo = binding.tagNoEditText.text.toString()
        val motherTagNo = binding.motherstagEditText.text.toString()
        val fatherTagNo = binding.fatherstagEditText.text.toString()
        val litterNo = binding.litterEditText.text.toString()
        val weight = binding.weightEditText.text.toString()
        val pigBreed = binding.autoCompleteTextView.text.toString()
        val gender = binding.autoCompletegenderTextView.text.toString()
        val piggroup = binding.autoCompletepiggroupTextView.text.toString()
        val pigobtained = binding.autoCompletehowpigwasobtanedtextView.text.toString()

        if (dateOfBirth.isEmpty()) {
            binding.dateofbirthInputLayout.error = "Please select a date of birth"
            isValid = false
        } else {
            binding.dateofbirthInputLayout.error = null
        }

        if (tagNo.length != 3 || !tagNo.all { it.isDigit() }) {
            binding.tagNoInputLayout.error = "Tag No must be a three-digit integer"
            isValid = false
        } else {
            binding.tagNoInputLayout.error = null
        }

        if (motherTagNo.length != 3 || !motherTagNo.all { it.isDigit() }) {
            binding.motherstagInputLayout.error = "Mother's Tag No must be a three-digit integer"
            isValid = false
        } else {
            binding.motherstagInputLayout.error = null
        }

        if (fatherTagNo.length != 3 || !fatherTagNo.all { it.isDigit() }) {
            binding.fathersstagInputLayout.error = "Father's Tag No must be a three-digit integer"
            isValid = false
        } else {
            binding.fathersstagInputLayout.error = null
        }

        if (gender == "Female") {
            if (!litterNo.all { it.isDigit() }) {
                binding.litterLayout.error = "Litter No must be an integer"
                isValid = false
            } else {
                binding.litterLayout.error = null
            }
        } else {
            if (litterNo.isNotEmpty()) {
                binding.litterLayout.error = "Litter No should be empty for non-female pigs"
                isValid = false
            } else {
                binding.litterLayout.error = null
            }
        }

        if (weight.toDoubleOrNull() == null) {
            binding.weightInputLayout.error = "Weight must be a decimal number"
            isValid = false
        } else {
            binding.weightInputLayout.error = null
        }

        // Validate pig breed selection
        binding.autoCompleteTextView.performValidation()
        if (!binding.autoCompleteTextView.validator.isValid(pigBreed)) {
            binding.autoCompleteTextInputLayout.error = "Please select a valid pig breed"
            isValid = false
        } else {
            binding.autoCompleteTextInputLayout.error = null
        }

        // Validate gender selection
        binding.autoCompletegenderTextView.performValidation()
        if (!binding.autoCompletegenderTextView.validator.isValid(gender)) {
            binding.autoCompletegenderTextInputLayout.error = "Please select a valid gender"
            isValid = false
        } else {
            binding.autoCompletegenderTextInputLayout.error = null
        }
        binding.autoCompletepiggroupTextView.performValidation()
        if (!binding.autoCompletepiggroupTextView.validator.isValid(piggroup)) {
            binding.autoCompletepiggroupTextInputLayout.error = "Please select a valid Pig Group"
            isValid = false
        } else {
            binding.autoCompletepiggroupTextInputLayout.error = null
        }
        binding.autoCompletehowpigwasobtanedtextView.performValidation()
        if (!binding.autoCompletehowpigwasobtanedtextView.validator.isValid(pigobtained)) {
            binding.autoCompletehowpigwasobtanedTextInputLayout.error = "Please select a valid option for how the pig was obtained"
            isValid = false
        } else {
            binding.autoCompletehowpigwasobtanedTextInputLayout.error = null
        }
        return isValid
    }

    private fun saveDataToFirestore() {
        val pigdetails = Pigdetails(
            pigbreed = binding.autoCompleteTextView.text.toString(),
            pigobtained = binding.autoCompletehowpigwasobtanedtextView.text.toString(),
            piggroup = binding.autoCompletepiggroupTextView.text.toString(),
            gender = binding.autoCompletegenderTextView.text.toString(),
            tag_no = binding.tagNoEditText.text.toString(),
            litter_no = binding.litterEditText.text.toString(),
            weight = binding.weightEditText.text.toString(),
            dateofbirth = binding.dateofbirthEditText.text.toString(),
            dateofentryonfarm = binding.dateofentryEditText.text.toString(),
            fatherstagno = binding.fatherstagEditText.text.toString(),
            motherstagno = binding.motherstagEditText.text.toString()
        )

        val user = auth.currentUser
        val userId = user?.uid

        if (userId != null) {
            val documentId = pigdetails.tag_no // Use tag_no as the document ID

            firestore.collection("users").document(userId).collection("stalls").document(args.stallNo)
                .collection("pigs").document(documentId) // Set the document ID to tag_no
                .set(pigdetails)
                .addOnSuccessListener {
                    addedPigDocumentId = documentId
                    clearInputs()
                    navigateToPigFragment()
                }
                .addOnFailureListener { e ->
                    binding.tagNoInputLayout.error = "Failed to save data: ${e.message}"
                }
        }
    }

    private fun clearInputs() {
        binding.autoCompleteTextView.text = null
        binding.tagNoEditText.text = null
        binding.litterEditText.text = null
        binding.weightEditText.text = null
        binding.dateofbirthEditText.text = null
        binding.dateofentryEditText.text = null
        binding.fatherstagEditText.text = null
        binding.motherstagEditText.text = null
        binding.autoCompletegenderTextView.text = null
        binding.autoCompletepiggroupTextView.text = null
        binding.autoCompletehowpigwasobtanedtextView.text = null
    }

    private fun navigateToPigFragment() {
        val action = AddpigFragmentDirections.actionAddpigFragmentToPigsFragment(args.stallNo)
        findNavController().navigate(action)
    }

    private fun showDatePickerDialog(onDateSet: (String) -> Unit) {
        val datePickerDialog = DatePickerDialog(requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDate = getString(R.string.date_format, selectedDay, selectedMonth + 1, selectedYear)
                onDateSet(formattedDate)
            },
            Calendar.getInstance().get(Calendar.YEAR),
            Calendar.getInstance().get(Calendar.MONTH),
            Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }
}
