package com.envizioners.dumpy.ownerclient.fragments.junkshop

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.envizioners.dumpy.ownerclient.OwnerActivity
import com.envizioners.dumpy.ownerclient.R
import com.envizioners.dumpy.ownerclient.RecyclerActivity
import com.envizioners.dumpy.ownerclient.StaffActivity
import com.envizioners.dumpy.ownerclient.adapter.junkshop.TradeProposalJViewAdapter
import com.envizioners.dumpy.ownerclient.preference.UserPreference
import com.envizioners.dumpy.ownerclient.response.tradeproposal.TradeProposalResult
import com.envizioners.dumpy.ownerclient.response.tradeproposal.TradeProposalResultItem
import com.envizioners.dumpy.ownerclient.viewmodels.junkshop.TradeProposalsVM
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

class TradeProposalsFragment : Fragment(), TradeProposalJViewAdapter.ClickListener {

    private lateinit var tradeProposalVA: TradeProposalJViewAdapter
    private lateinit var tradeSizeNotice: TextView
    private lateinit var loadingProgressbar: ProgressBar
    private val tpVM: TradeProposalsVM by lazy {
        ViewModelProvider(this).get(TradeProposalsVM::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CoroutineScope(Dispatchers.Main).launch {
            val establishmentID = UserPreference.getUserBusinessID(requireContext(), "USER_BUSINESS_ID")
            tpVM.getPendingProposals(establishmentID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.f_j_trade_proposals, container, false)

        tradeSizeNotice = view.findViewById(R.id.f_j_no_pending_proposal_notice)
        loadingProgressbar = view.findViewById(R.id.f_j_trade_proposal_loading)

        tpVM.ppLD.observe(viewLifecycleOwner){ response ->
            if (response.size == 0){
                tradeSizeNotice.visibility = View.VISIBLE
            }
            loadingProgressbar.visibility = View.GONE
            initRecyclerView(view, response)
        }
        return view
    }

    private fun initRecyclerView(view: View, tradeProposalList: TradeProposalResult){
        val recyclerView = view.findViewById<RecyclerView>(R.id.f_j_trade_proposals_list)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        tradeProposalVA = TradeProposalJViewAdapter(tradeProposalList, this)
        recyclerView.adapter = tradeProposalVA
    }

    override fun onItemClick(tradeProposal: TradeProposalResultItem) {
        val args = Bundle()
        args.putSerializable("TP_ITEM", tradeProposal)
        val tradeProposalDetails = TradeProposalDetails()
        tradeProposalDetails.arguments = args
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.addToBackStack(null)
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right)
        transaction.replace(R.id.fragment_container, tradeProposalDetails).commit()
    }

    override fun onResume() {
        super.onResume()
        try {
            (requireActivity() as OwnerActivity).updateActionBarTitle("Trade Proposals")
        }catch (exception: Exception){
            (requireActivity() as StaffActivity).updateActionBarTitle("Trade Proposals")
        }
    }
}