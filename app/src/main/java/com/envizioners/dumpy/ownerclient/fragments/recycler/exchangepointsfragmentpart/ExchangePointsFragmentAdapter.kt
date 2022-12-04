package com.envizioners.dumpy.ownerclient.fragments.recycler.exchangepointsfragmentpart

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class ExchangePointsFragmentAdapter(
    fragmentList: ArrayList<Fragment>,
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
): FragmentStateAdapter(
    fragmentManager,
    lifecycle
) {
    private val listOfFragments = fragmentList
    override fun getItemCount(): Int = listOfFragments.size
    override fun createFragment(position: Int): Fragment = listOfFragments[position]
}