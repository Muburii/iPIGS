package com.example.zufffinalyear.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.zufffinalyear.R
import com.example.zufffinalyear.adapter.EventsAdapter
import com.example.zufffinalyear.databinding.FragmentEventsBinding
import com.google.android.material.tabs.TabLayoutMediator

class EventsFragment : Fragment() {

    private var _binding: FragmentEventsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = EventsAdapter(childFragmentManager, lifecycle)
        binding.eventsviewpager.adapter = adapter

        // Define the tab titles
        val tabTitles = arrayOf(getString(R.string.individualevents), getString(R.string.massevents))

        TabLayoutMediator(binding.tablayout, binding.eventsviewpager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
