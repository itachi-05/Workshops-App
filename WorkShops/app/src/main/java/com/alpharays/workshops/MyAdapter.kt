package com.alpharays.workshops


import androidx.viewpager2.adapter.FragmentStateAdapter
import com.alpharays.workshops.MainActivity
import com.alpharays.workshops.ui.dashboard.DashboardFragment
import com.alpharays.workshops.ui.workshops.WorkshopsFragment


class MyAdapter(fragmentActivity: MainActivity) : FragmentStateAdapter(fragmentActivity) {
    private val fragmentList = arrayListOf(
        DashboardFragment(),
        WorkshopsFragment()
    )

    override fun getItemCount() = fragmentList.size

    override fun createFragment(position: Int) = fragmentList[position]
}
