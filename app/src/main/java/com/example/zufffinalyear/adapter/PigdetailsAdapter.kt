package com.example.zufffinalyear.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.zufffinalyear.fragments.PigEventFragment
import com.example.zufffinalyear.fragments.PigdetailsFragment


class PigdetailsAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return 2 // Number of tabs
    }
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> PigdetailsFragment()// Replace with your actual fragment class
            1 -> PigEventFragment() // Replace with your actual fragment class
            else -> throw IllegalStateException("Unexpected position: $position")
        }
    }
}
