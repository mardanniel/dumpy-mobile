package com.envizioners.dumpy.ownerclient.fragments.shared

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.envizioners.dumpy.ownerclient.OwnerActivity
import com.envizioners.dumpy.ownerclient.R
import com.envizioners.dumpy.ownerclient.RecyclerActivity
import com.envizioners.dumpy.ownerclient.StaffActivity
import com.envizioners.dumpy.ownerclient.adapter.shared.LeaderboardViewAdapter
import com.envizioners.dumpy.ownerclient.preference.UserPreference
import com.envizioners.dumpy.ownerclient.response.shared.LeaderboardPlacer
import com.envizioners.dumpy.ownerclient.response.shared.LeaderboardPlacerResultBody
import com.envizioners.dumpy.ownerclient.viewmodels.shared.DashboardVM
import com.envizioners.dumpy.ownerclient.viewmodels.shared.LeaderboardVM
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

class LeaderboardFragment : Fragment() {

    private lateinit var leaderboardViewAdapter: LeaderboardViewAdapter
    private lateinit var noLeaderboard: TextView
    private lateinit var lbpb: ProgressBar
    private val leaderboardVM: LeaderboardVM by lazy {
        ViewModelProvider(this).get(LeaderboardVM::class.java)
    }
    private var userType: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CoroutineScope(Dispatchers.Main).launch {
            userType = UserPreference.getUserType(requireContext(), "USER_TYPE")
        }
        leaderboardVM.getLeaderboard()
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val leaderboardView = inflater.inflate(R.layout.f_leaderboard, container, false)
        noLeaderboard = leaderboardView.findViewById(R.id.f_r_leaderboard_empty)
        lbpb = leaderboardView.findViewById(R.id.f_leaderboard_pb)
        leaderboardVM.leaderboardMLD.observe(viewLifecycleOwner) { response ->
            initRecyclerView(leaderboardView, response)
            noLeaderboard.apply {
                if (response.isEmpty()){
                    if (userType != "DUMPY_RECYCLER"){
                        noLeaderboard.text = "Nobody's on the list yet...\n(ﾉ◕ヮ◕)ﾉ*:･ﾟ✧"
                    }
                    this.visibility = View.VISIBLE
                }
            }
            lbpb.visibility = View.GONE
        }
        return leaderboardView
    }

    private fun initRecyclerView(view: View, topLeaderboardPlacers: List<LeaderboardPlacerResultBody>){
        val recyclerView = view.findViewById<RecyclerView>(R.id.f_leaderboard_rv)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        leaderboardViewAdapter = LeaderboardViewAdapter(topLeaderboardPlacers)
        recyclerView.adapter = leaderboardViewAdapter
    }

    override fun onResume() {
        super.onResume()
        try {
            (requireActivity() as RecyclerActivity).updateActionBarTitle("Leaderboard")
        }catch (exception: Exception){
            try {
                (requireActivity() as OwnerActivity).updateActionBarTitle("Leaderboard")
            }catch (exception: Exception){
                (requireActivity() as StaffActivity).updateActionBarTitle("Leaderboard")
            }
        }
    }
}