package com.envizioners.dumpy.ownerclient.fragments.junkshop

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.envizioners.dumpy.ownerclient.OwnerActivity
import com.envizioners.dumpy.ownerclient.R
import com.envizioners.dumpy.ownerclient.StaffActivity
import com.envizioners.dumpy.ownerclient.adapter.recycler.TradeProposalDetailsViewAdapter
import com.envizioners.dumpy.ownerclient.fragments.staff.TradeProposalAction
import com.envizioners.dumpy.ownerclient.preference.UserPreference
import com.envizioners.dumpy.ownerclient.response.tradeproposal.TradeProposalResultItem
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

class TradeProposalDetails : Fragment() {

    // Components
    private lateinit var tradeProposalDetailsViewAdapter: TradeProposalDetailsViewAdapter
    private lateinit var tpdRecyclerName: TextView
    private lateinit var tpdCreationDT: TextView
    private lateinit var tpdAction: Button
    private lateinit var tpdLoading: ProgressBar

    // Data Transformation Tool
    private lateinit var gson: Gson

    // Arguments
    private lateinit var tradeProposalDetails: TradeProposalResultItem

    // Details
    private var userID: Int = 0
    private var userEmail: String = ""
    private lateinit var tradeItems: List<String>
    private lateinit var tradeQuantities: List<String>
    private lateinit var tradeLabels: List<String>
    private lateinit var tradeTypes: List<String>
    private lateinit var tradeImageNames: List<String>

    private var userTYPE = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gson = Gson()
        tradeProposalDetails = arguments?.getSerializable("TP_ITEM") as TradeProposalResultItem
        userID = tradeProposalDetails.user_id.toInt()
        userEmail = tradeProposalDetails.user_email
        tradeItems = gson.fromJson(tradeProposalDetails.tp_items, mutableListOf<String>()::class.java)
        tradeQuantities = gson.fromJson(tradeProposalDetails.tp_quantities, mutableListOf<String>()::class.java)
        tradeLabels = gson.fromJson(tradeProposalDetails.tp_labels, mutableListOf<String>()::class.java)
        tradeTypes = gson.fromJson(tradeProposalDetails.tp_types, mutableListOf<String>()::class.java)
        tradeImageNames = gson.fromJson(tradeProposalDetails.tp_image_names, mutableListOf<String>()::class.java)
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.f_j_trade_proposal_details, container, false)

        // Components
        tpdRecyclerName = view.findViewById(R.id.f_j_tpd_recycler_name)
        tpdCreationDT = view.findViewById(R.id.f_j_tpd_creation_dt)
        tpdAction = view.findViewById(R.id.f_j_action_btn)
        tpdLoading = view.findViewById(R.id.f_j_tpd_loading)

        tpdRecyclerName.text = "${tradeProposalDetails.user_fname} ${tradeProposalDetails.user_mname} ${tradeProposalDetails.user_lname}"
        tpdCreationDT.text = "Creation Datetime: ${tradeProposalDetails.tp_date_time}"

        CoroutineScope(Dispatchers.Main).launch {
            userTYPE = UserPreference.getUserType(requireContext(), "USER_TYPE")
            tpdAction.apply {
                if(userTYPE == "DUMPY_STAFF"){
                    this.visibility = View.VISIBLE
                    this.setOnClickListener {
                        val args = Bundle()
                        args.putString("tp_id", tradeProposalDetails.tp_id)
                        val tpAction = TradeProposalAction()
                        tpAction.arguments = args
                        requireActivity().supportFragmentManager.beginTransaction()
                            .addToBackStack(null)
                            .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right)
                            .replace(R.id.fragment_container, tpAction).commit()
                    }
                }
            }
        }
        initRecyclerView(view)
        tpdLoading.visibility = View.GONE
        return view
    }

    private fun initRecyclerView(view: View){
        val recyclerView = view.findViewById<RecyclerView>(R.id.f_j_tpd_item_list)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        tradeProposalDetailsViewAdapter = TradeProposalDetailsViewAdapter(
            tradeItems,
            tradeQuantities,
            tradeLabels,
            tradeTypes,
            tradeImageNames,
            userEmail,
            requireContext(),
            requireActivity().supportFragmentManager
        )
        recyclerView.adapter = tradeProposalDetailsViewAdapter
    }

    override fun onResume() {
        super.onResume()
        try {
            (requireActivity() as OwnerActivity).updateActionBarTitle("Trade Proposals Overview")
        }catch (exception: Exception){
            (requireActivity() as StaffActivity).updateActionBarTitle("Trade Proposals Overview")
        }
    }
}