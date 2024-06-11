package com.example.zufffinalyear.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.zufffinalyear.R
import com.example.zufffinalyear.adapter.PigdetailsAdapter
import com.example.zufffinalyear.databinding.FragmentProfileBinding
import com.google.android.material.tabs.TabLayoutMediator

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private var stallId: String? = null
    private var documentId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        stallId = arguments?.getString("stallId")
        documentId = arguments?.getString("documentId")

        val adapter = PigdetailsAdapter(childFragmentManager, lifecycle, stallId, documentId)
        binding.detailsviewpager.adapter = adapter

        val tabTitles = arrayOf(getString(R.string.Details), getString(R.string.Events))
        TabLayoutMediator(binding.tablayout, binding.detailsviewpager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
