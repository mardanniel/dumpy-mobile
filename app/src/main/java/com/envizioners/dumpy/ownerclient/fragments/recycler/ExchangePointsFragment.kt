package com.envizioners.dumpy.ownerclient.fragments.recycler

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.envizioners.dumpy.ownerclient.R
import com.envizioners.dumpy.ownerclient.RecyclerActivity
import com.envizioners.dumpy.ownerclient.fragments.recycler.exchangepointsfragmentpart.CouponsList
import com.envizioners.dumpy.ownerclient.fragments.recycler.exchangepointsfragmentpart.ExchangePointsFragmentAdapter
import com.envizioners.dumpy.ownerclient.fragments.recycler.exchangepointsfragmentpart.ExchangePointsList
import com.envizioners.dumpy.ownerclient.preference.UserPreference
import com.envizioners.dumpy.ownerclient.viewmodels.recycler.CreateTradeVM
import com.envizioners.dumpy.ownerclient.viewmodels.recycler.ExchangePointsVM
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ExchangePointsFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.f_r_exchange_points, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val fragmentList = arrayListOf(
            ExchangePointsList(),
            CouponsList()
        )

        val adapter = ExchangePointsFragmentAdapter(
            fragmentList,
            requireActivity().supportFragmentManager,
            lifecycle
        )

        val viewPager2 = view.findViewById<ViewPager2>(R.id.f_r_ep_viewpager)

        viewPager2.adapter = adapter
        val tabLayout = view.findViewById<TabLayout>(R.id.f_r_ep_tablayout)
        TabLayoutMediator(tabLayout, viewPager2) { tab, pos ->
            when(pos){
                0 -> {
                    tab.text = "Exchange Points"
                }
                1 -> {
                    tab.text = "Coupons"
                }
            }
        }.attach()
    }
}