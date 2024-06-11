package com.example.zufffinalyear.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.zufffinalyear.R
import com.example.zufffinalyear.adapter.FeedsAdapter
import com.example.zufffinalyear.databinding.FragmentFeedsBinding
import com.google.android.material.tabs.TabLayoutMediator

class FeedsFragment : Fragment() {

    private var _binding: FragmentFeedsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFeedsBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = FeedsAdapter(childFragmentManager, lifecycle)
        binding.feedsviewpager.adapter = adapter
        // Define the tab titles
        val tabTitles = arrayOf(getString(R.string.Additions), getString(R.string.Reductions))

        TabLayoutMediator(binding.feedstablayout, binding.feedsviewpager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
