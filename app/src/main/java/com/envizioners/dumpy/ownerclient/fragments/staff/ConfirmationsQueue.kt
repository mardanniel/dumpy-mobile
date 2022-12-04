package com.envizioners.dumpy.ownerclient.fragments.staff

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.envizioners.dumpy.ownerclient.R
import com.envizioners.dumpy.ownerclient.RecyclerActivity
import com.envizioners.dumpy.ownerclient.StaffActivity
import com.envizioners.dumpy.ownerclient.adapter.junkshop.TradeProposalJViewAdapter
import com.envizioners.dumpy.ownerclient.fragments.junkshop.TradeProposalDetails
import com.envizioners.dumpy.ownerclient.preference.UserPreference
import com.envizioners.dumpy.ownerclient.response.tradeproposal.CreateTradeItem
import com.envizioners.dumpy.ownerclient.response.tradeproposal.TradeProposalResult
import com.envizioners.dumpy.ownerclient.response.tradeproposal.TradeProposalResultItem
import com.envizioners.dumpy.ownerclient.utils.FragmentUtils
import com.envizioners.dumpy.ownerclient.viewmodels.junkshop.TradeProposalsVM
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ConfirmationsQueue : Fragment(), TradeProposalJViewAdapter.ClickListener {

    private lateinit var tradeProposalVA: TradeProposalJViewAdapter
    private lateinit var tradeSizeNotice: TextView
    private lateinit var loadingProgressBar: ProgressBar
    private val tpVM: TradeProposalsVM by lazy {
        ViewModelProvider(this).get(TradeProposalsVM::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CoroutineScope(Dispatchers.Main).launch {
            val establishmentID: Int = UserPreference.getUserBusinessID(requireContext(), "USER_BUSINESS_ID")
            tpVM.getProceededProposals(establishmentID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.f_s_confirmations_queue, container, false)

        tradeSizeNotice = view.findViewById(R.id.f_j_no_cq_notice)
        loadingProgressBar = view.findViewById(R.id.f_j_cq_loading)

        tpVM.ppLD.observe(viewLifecycleOwner) { response ->
            if (response.size == 0){
                tradeSizeNotice.visibility = View.VISIBLE
            }
            loadingProgressBar.visibility = View.GONE
            initRecyclerView(view, response)
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity()
            .supportFragmentManager
            .setFragmentResultListener(
                "doReload",
                viewLifecycleOwner
            ) { _, bundle ->
                val shouldReload = bundle.getBoolean("shouldReload")
                if (shouldReload){
                    FragmentUtils.refreshFragment(requireContext())
                }
            }
    }

    private fun initRecyclerView(view: View, tradeProposalList: TradeProposalResult){
        val recyclerView = view.findViewById<RecyclerView>(R.id.f_s_confirmations_list)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        tradeProposalVA = TradeProposalJViewAdapter(tradeProposalList, this)
        recyclerView.adapter = tradeProposalVA
    }

    override fun onItemClick(tradeProposal: TradeProposalResultItem) {
        val args = Bundle()
        args.putSerializable("TP_ITEM_MC", tradeProposal)
        val manageConfirmation = ManageConfirmation()
        manageConfirmation.arguments = args
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.addToBackStack(null)
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right)
        transaction.replace(R.id.fragment_container, manageConfirmation).commit()
    }

    override fun onResume() {
        super.onResume()
        var currActivity = requireActivity() as StaffActivity
        currActivity.updateActionBarTitle("Confirmations")
    }
}