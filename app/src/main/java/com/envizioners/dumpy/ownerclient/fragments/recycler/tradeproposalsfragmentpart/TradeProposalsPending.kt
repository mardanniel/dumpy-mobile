package com.envizioners.dumpy.ownerclient.fragments.recycler.tradeproposalsfragmentpart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.envizioners.dumpy.ownerclient.R
import com.envizioners.dumpy.ownerclient.adapter.recycler.TradeProposalViewAdapterR
import com.envizioners.dumpy.ownerclient.preference.UserPreference
import com.envizioners.dumpy.ownerclient.response.recycler_main.TradeProposalsRResultBody
import com.envizioners.dumpy.ownerclient.viewmodels.recycler.TradeProposalsVM
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TradeProposalsPending : Fragment(), TradeProposalViewAdapterR.ClickListener {

    private lateinit var tradeProposalViewAdapterR: TradeProposalViewAdapterR
    private lateinit var emptyListText: TextView
    private lateinit var pendingProgressBar: ProgressBar

    private var userID: Int = 0
    private var pendingListOfTradeProposals = mutableListOf<TradeProposalsRResultBody>()

    private val tradeProposalsVM: TradeProposalsVM by lazy {
        ViewModelProvider(this).get(TradeProposalsVM::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CoroutineScope(Dispatchers.Main).launch {
            userID = UserPreference.getUserID(requireContext(), "USER_ID")
            tradeProposalsVM.getTradeProposals(userID, 69   )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.f_r_trade_proposals_pending, container, false)

        emptyListText = view.findViewById(R.id.f_r_tp_empty_pending_list_text)
        pendingProgressBar = view.findViewById(R.id.pending_pb)

        tradeProposalsVM.tpmld.observe(viewLifecycleOwner) { response ->
            pendingListOfTradeProposals = response as MutableList<TradeProposalsRResultBody>

            emptyListText.apply {
                if (pendingListOfTradeProposals.size == 0){
                    this.visibility = View.VISIBLE
                }
            }

            initRecyclerView(view)
            pendingProgressBar.visibility = View.GONE
        }
        return view
    }

    private fun initRecyclerView(view: View){
        val recyclerView = view.findViewById<RecyclerView>(R.id.f_r_tp_pending_list)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        tradeProposalViewAdapterR = TradeProposalViewAdapterR(
            pendingListOfTradeProposals,
            "#ff009688",
            this
        )
        recyclerView.adapter = tradeProposalViewAdapterR
    }

    override fun onItemClick(tradeProposal: TradeProposalsRResultBody) {
        val bundle = Bundle()
        val tradeProposalDetails = TradeProposalDetails()
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        bundle.putSerializable("TP_DETAILS", tradeProposal)
        tradeProposalDetails.arguments = bundle
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right)
        transaction.replace(R.id.fragment_container, tradeProposalDetails)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}