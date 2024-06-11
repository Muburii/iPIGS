package com.example.zufffinalyear.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.zufffinalyear.fragments.EventsFragment
import com.example.zufffinalyear.fragments.PigdetailsFragment

class PigdetailsAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    private val stallId: String?,
    private val documentId: String?
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> PigdetailsFragment().apply {
                arguments = Bundle().apply {
                    putString("stallId", stallId)
                    putString("documentId", documentId)
                }
            }
            1 -> EventsFragment()
            else -> throw IllegalStateException("Unexpected position $position")
        }
    }
}
