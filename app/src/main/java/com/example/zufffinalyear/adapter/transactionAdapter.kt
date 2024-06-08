package com.example.zufffinalyear.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.zufffinalyear.fragments.ExpenseFragment
import com.example.zufffinalyear.fragments.IncomeFragment

class transactionAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return 2 // Number of tabs
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> IncomeFragment()
            1 -> ExpenseFragment()
            else -> throw IllegalStateException("Unexpected position: $position")
        }
    }
}
