package com.envizioners.dumpy.ownerclient.fragments.recycler.tradeproposalsfragmentpart

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
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

class TradeProposalsCompleted : Fragment(), TradeProposalViewAdapterR.ClickListener {

    private lateinit var tradeProposalViewAdapterR: TradeProposalViewAdapterR
    private lateinit var emptyListText: TextView
    private lateinit var completedProgressBar: ProgressBar

    private var userID: Int = 0
    private var completedListOfTradeProposals = mutableListOf<TradeProposalsRResultBody>()

    private val tradeProposalsVM: TradeProposalsVM by lazy {
        ViewModelProvider(this).get(TradeProposalsVM::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CoroutineScope(Dispatchers.Main).launch {
            userID = UserPreference.getUserID(requireContext(), "USER_ID")
            tradeProposalsVM.getTradeProposals(userID, 73)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.f_r_trade_proposals_completed, container, false)

        emptyListText = view.findViewById(R.id.f_r_tp_empty_completed_list_text)
        completedProgressBar = view.findViewById(R.id.completed_pb)

        tradeProposalsVM.tpmld.observe(viewLifecycleOwner) { response ->
            completedListOfTradeProposals = response as MutableList<TradeProposalsRResultBody>

            emptyListText.apply {
                if (completedListOfTradeProposals.size == 0){
                    this.visibility = View.VISIBLE
                }
            }

            initRecyclerView(view)
            completedProgressBar.visibility = View.GONE
        }
        return view
    }

    private fun initRecyclerView(view: View){
        val recyclerView = view.findViewById<RecyclerView>(R.id.f_r_tp_completed_list)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        tradeProposalViewAdapterR = TradeProposalViewAdapterR(
            completedListOfTradeProposals,
            "#ff4CAF50",
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