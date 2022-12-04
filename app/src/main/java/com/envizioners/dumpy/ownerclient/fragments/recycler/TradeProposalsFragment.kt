package com.envizioners.dumpy.ownerclient.fragments.recycler

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.envizioners.dumpy.ownerclient.R
import com.envizioners.dumpy.ownerclient.RecyclerActivity
import com.envizioners.dumpy.ownerclient.fragments.recycler.tradeproposalsfragmentpart.*
import com.envizioners.dumpy.ownerclient.preference.UserPreference
import com.envizioners.dumpy.ownerclient.response.recycler_main.TradeProposalsRResultBody
import com.envizioners.dumpy.ownerclient.utils.LoadingDialog
import com.envizioners.dumpy.ownerclient.viewmodels.recycler.TradeProposalsVM
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TradeProposalsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.f_r_trade_proposals, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        var currActivity = requireActivity() as RecyclerActivity
        currActivity.updateActionBarTitle("Trade Proposals")
        val fragmentList = arrayListOf<Fragment>(
            TradeProposalsPending(),
            TradeProposalsAccepted(),
            TradeProposalsProceeded(),
            TradeProposalsCompleted(),
            TradeProposalsDeclined(),
            TradeProposalsCancelled()
        )
        val adapter = TradeProposalByStatusFragmentAdapter(
            fragmentList,
            childFragmentManager,
            viewLifecycleOwner.lifecycle
        )

        val viewPager2 = view.findViewById<ViewPager2>(R.id.f_r_tp_viewpager)
        viewPager2.adapter = adapter
        val tabLayout = view.findViewById<TabLayout>(R.id.f_r_tp_tablayout)
        TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
            when(position){
                0 -> tab.setIcon(R.drawable.ic_tp_pending)
                1 -> tab.setIcon(R.drawable.ic_tp_accepted)
                2 -> tab.setIcon(R.drawable.ic_tp_proceed)
                3 -> tab.setIcon(R.drawable.ic_tp_completed)
                4 -> tab.setIcon(R.drawable.ic_tp_declined)
                5 -> tab.setIcon(R.drawable.ic_tp_cancelled)
            }
        }.attach()
    }
}