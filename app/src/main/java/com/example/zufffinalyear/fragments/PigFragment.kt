package com.example.zufffinalyear.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.zufffinalyear.R
import com.example.zufffinalyear.databinding.FragmentPigBinding

class PigFragment : Fragment() {

    private var _binding: FragmentPigBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPigBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pigBreeds = resources.getStringArray(R.array.pig_breeds_list)
        val pigBreedsAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, pigBreeds)
        binding.autoCompleteTextView.setAdapter(pigBreedsAdapter)

        binding.fabAddpig.setOnClickListener {
            findNavController().navigate(R.id.action_pigsFragment_to_addpigFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
