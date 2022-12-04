package com.envizioners.dumpy.ownerclient.fragments.recycler.tradeproposalsfragmentpart

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
import com.envizioners.dumpy.ownerclient.R
import com.envizioners.dumpy.ownerclient.adapter.recycler.TradeProposalViewAdapterR
import com.envizioners.dumpy.ownerclient.preference.UserPreference
import com.envizioners.dumpy.ownerclient.response.recycler_main.TradeProposalsRResultBody
import com.envizioners.dumpy.ownerclient.utils.FragmentUtils
import com.envizioners.dumpy.ownerclient.viewmodels.recycler.TradeProposalsVM
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TradeProposalsAccepted : Fragment(), TradeProposalViewAdapterR.ClickListener {

    private lateinit var tradeProposalViewAdapterR: TradeProposalViewAdapterR
    private lateinit var emptyListText: TextView
    private lateinit var acceptedProgressBar: ProgressBar

    private var userID: Int = 0
    private var acceptedListOfTradeProposals = mutableListOf<TradeProposalsRResultBody>()

    private val tradeProposalsVM: TradeProposalsVM by lazy {
        ViewModelProvider(this).get(TradeProposalsVM::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CoroutineScope(Dispatchers.Main).launch {
            userID = UserPreference.getUserID(requireContext(), "USER_ID")
            tradeProposalsVM.getTradeProposals(userID, 70)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.f_r_trade_proposals_accepted, container, false)

        emptyListText = view.findViewById(R.id.f_r_tp_empty_accepted_list_text)
        acceptedProgressBar = view.findViewById(R.id.accepted_pb)

        tradeProposalsVM.tpmld.observe(viewLifecycleOwner) { response ->
            acceptedListOfTradeProposals = response as MutableList<TradeProposalsRResultBody>

            emptyListText.apply {
                if (acceptedListOfTradeProposals.size == 0){
                    this.visibility = View.VISIBLE
                }
            }

            initRecyclerView(view)
            acceptedProgressBar.visibility = View.GONE
        }
        return view
    }

    private fun initRecyclerView(view: View){
        val recyclerView = view.findViewById<RecyclerView>(R.id.f_r_tp_accepted_list)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        tradeProposalViewAdapterR = TradeProposalViewAdapterR(
            acceptedListOfTradeProposals,
            "#ff03A9F4",
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