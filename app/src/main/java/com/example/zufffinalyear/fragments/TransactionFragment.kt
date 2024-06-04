package com.example.zufffinalyear.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.zufffinalyear.R
import com.example.zufffinalyear.adapter.transactionAdapter
import com.example.zufffinalyear.databinding.FragmentTransactionBinding
import com.google.android.material.tabs.TabLayoutMediator

class TransactionFragment : Fragment() {

    private var _binding: FragmentTransactionBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransactionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = transactionAdapter(childFragmentManager, lifecycle)
        binding.transactionviewpager.adapter = adapter

        // Define the tab titles
        val tabTitles = arrayOf(getString(R.string.income), getString(R.string.expense))

        TabLayoutMediator(binding.tablayout, binding.transactionviewpager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
