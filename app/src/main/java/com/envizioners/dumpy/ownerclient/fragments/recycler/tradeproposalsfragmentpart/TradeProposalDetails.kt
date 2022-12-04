package com.envizioners.dumpy.ownerclient.fragments.recycler.tradeproposalsfragmentpart

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.envizioners.dumpy.ownerclient.R
import com.envizioners.dumpy.ownerclient.adapter.recycler.TradeProposalDetailsViewAdapter
import com.envizioners.dumpy.ownerclient.adapter.recycler.TradeProposalViewAdapterR
import com.envizioners.dumpy.ownerclient.fragments.recycler.CreateTradeForm
import com.envizioners.dumpy.ownerclient.fragments.shared.DashboardFragment
import com.envizioners.dumpy.ownerclient.preference.UserPreference
import com.envizioners.dumpy.ownerclient.response.points.CouponsResult
import com.envizioners.dumpy.ownerclient.response.recycler_main.TradeProposalsRResultBody
import com.envizioners.dumpy.ownerclient.utils.LoadingDialog
import com.envizioners.dumpy.ownerclient.viewmodels.recycler.RecyclerTradeProposalActionVM
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.w3c.dom.Text

class TradeProposalDetails : Fragment() {

    // Components
    private lateinit var tradeProposalDetailsViewAdapter: TradeProposalDetailsViewAdapter
    private lateinit var tpdStatus: TextView
    private lateinit var tradingTo: TextView
    private lateinit var tpdCreationDT: TextView
    private lateinit var tpdAcceptedDT: TextView
    private lateinit var tpdCancelDT: TextView
    private lateinit var tpdProceedDT: TextView
    private lateinit var tpdRatingConstraint: ConstraintLayout
    private lateinit var tpdRatingBar: RatingBar
    private lateinit var tpdSendRating: Button
    private lateinit var tpdCompleteDT: TextView
    private lateinit var tpdItemsLabel: TextView
    private lateinit var tpdMessage: TextView
    private lateinit var tpdMainParentConstraint: ConstraintLayout
    private lateinit var tpdActionsConstraint: ConstraintLayout
    private lateinit var tpdInfoConstraint: ConstraintLayout
    private lateinit var tpdCancelBTN: Button
    private lateinit var tpdProceedBTN: Button
    private lateinit var tpdRemoveBTN: Button
    private lateinit var loadingDialog: LoadingDialog

    // Component Manipulator
    private lateinit var constraintSet: ConstraintSet

    // Data Transformation Tool
    private lateinit var gson: Gson

    // Arguments
    private lateinit var tradeProposalDetails: TradeProposalsRResultBody

    // Details
    private var userID: Int = 0
    private var userEmail: String = ""
    private lateinit var tradeItems: List<String>
    private lateinit var tradeQuantities: List<String>
    private lateinit var tradeLabels: List<String>
    private lateinit var tradeTypes: List<String>
    private lateinit var tradeImageNames: List<String>
    private var ratingVal: Float = 0.0F

    private val tradeProposalActionVM: RecyclerTradeProposalActionVM by lazy {
        ViewModelProvider(this).get(RecyclerTradeProposalActionVM::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CoroutineScope(Dispatchers.IO).launch {
            userID = UserPreference.getUserID(requireContext(), "USER_ID")
            userEmail = UserPreference.getUserEmail(requireContext(), "USER_EMAIL")
        }
        gson = Gson()
        arguments?.let {
            tradeProposalDetails = it.getSerializable("TP_DETAILS") as TradeProposalsRResultBody
        }
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
        val view = inflater.inflate(R.layout.f_r_trade_proposal_details, container, false)
        // TextViews
        tpdStatus = view.findViewById(R.id.trade_proposal_details_status)
        tradingTo = view.findViewById(R.id.tpd_traded_to)
        tpdCreationDT = view.findViewById(R.id.tpd_creation_dt_info)
        tpdAcceptedDT = view.findViewById(R.id.tpd_accepted_dt_info)
        tpdCancelDT = view.findViewById(R.id.tpd_cancel_dt_info)
        tpdProceedDT = view.findViewById(R.id.tpd_proceeded_dt_info)
        tpdCompleteDT = view.findViewById(R.id.tpd_complete_dt_info)
        tpdRatingConstraint = view.findViewById(R.id.tpd_rating_constraint)
        tpdSendRating = view.findViewById(R.id.tpd_send_rating)
        tpdRatingBar = view.findViewById(R.id.tpd_completion_ratingbar)
        tpdItemsLabel = view.findViewById(R.id.trade_proposal_details_items_label)
        tpdMessage = view.findViewById(R.id.tpd_staff_message)

        // ConstraintLayouts
        tpdMainParentConstraint = view.findViewById(R.id.tpd_main_parent_constraint)
        tpdActionsConstraint = view.findViewById(R.id.tpd_actions_constraints)
        tpdInfoConstraint = view.findViewById(R.id.tpd_info_constraints)

        // Action Buttons
        tpdCancelBTN = view.findViewById(R.id.tpd_cancel_dt)
        tpdProceedBTN = view.findViewById(R.id.tpd_proceed_dt)
        tpdRemoveBTN = view.findViewById(R.id.tpd_remove_dt)
        loadingDialog = LoadingDialog(requireActivity())

        // ConstraintSet
        constraintSet = ConstraintSet()

        tpdStatus.text = "STATUS: ${tradeProposalDetails.tp_status}"
        tradingTo.apply {
            if (tradeProposalDetails.tp_status != "COMPLETED"){
                this.text = "Trading to: ${tradeProposalDetails.js_name}"
            }else{
                this.text = "Traded to: ${tradeProposalDetails.js_name}"
            }
        }
        tpdCreationDT.apply {
            this.text = "Creation Datetime: ${tradeProposalDetails.tp_creation_date_time}"
        }
        tpdAcceptedDT.apply {
            if (tradeProposalDetails.tp_accepted_date_time != null){
                this.visibility = View.VISIBLE
                if (tradeProposalDetails.tp_status != "DECLINED"){
                    this.text = "Accepted Datetime: ${tradeProposalDetails.tp_accepted_date_time}"
                }else{
                    this.text = "Declined Datetime: ${tradeProposalDetails.tp_accepted_date_time}"
                }
            }
        }
        tpdCancelDT.apply {
            if (tradeProposalDetails.tp_cancel_date_time != null){
                this.visibility = View.VISIBLE
                this.text = "Cancelled Datetime: ${tradeProposalDetails.tp_cancel_date_time}"
            }
        }
        tpdProceedDT.apply {
            if (tradeProposalDetails.tp_proceed_date_time != null){
                this.visibility = View.VISIBLE
                this.text = "Proceed Datetime: ${tradeProposalDetails.tp_proceed_date_time}"
            }
        }
        tpdCompleteDT.apply {
            if (tradeProposalDetails.tp_finish_date_time != null){
                this.visibility = View.VISIBLE
                this.text = "Completion Datetime: ${tradeProposalDetails.tp_finish_date_time}"
            }
        }
        tpdRatingConstraint.apply {
            if (tradeProposalDetails.tp_status == "COMPLETED"){
                if (tradeProposalDetails.tp_rating?.toFloat()!! == 0.0F){
                    this.visibility = View.VISIBLE
                    tpdRatingBar.setOnRatingBarChangeListener { _, fl, _ ->
                        ratingVal = fl
                    }
                    tpdSendRating.setOnClickListener{
                        if (ratingVal < 0.5F){
                            Toast.makeText(requireContext(), "Minimum rating value is 1!", Toast.LENGTH_SHORT).show()
                        }else{
                            loadingDialog.setLoadingScreenText("Sending your rating...")
                            loadingDialog.startLoading()
                            tradeProposalActionVM.insertRating(
                                userID,
                                tradeProposalDetails.tp_id.toInt(),
                                ratingVal
                            )
                        }
                    }
                }
            }
        }
        tpdItemsLabel.apply {
            if (tradeProposalDetails.tp_status != "COMPLETED"){
                this.text = "Items to be traded"
            }else{
                this.text = "Items traded"
            }
        }
        tpdMessage.apply {
            if (tradeProposalDetails.tp_status == "ACCEPTED"){
                this.visibility = View.VISIBLE
                this.text = "Staff Message: \"${tradeProposalDetails.tp_message}\""
            }
        }
        when(tradeProposalDetails.tp_status){
            "PENDING", "PROCEEDED" -> {
                tpdCancelBTN.visibility = View.VISIBLE
                constraintSet.clone(tpdActionsConstraint)
                constraintSet.connect(
                    tpdCancelBTN.id,
                    ConstraintSet.RIGHT,
                    tpdActionsConstraint.id,
                    ConstraintSet.RIGHT
                )
            }
            "ACCEPTED" -> {
                tpdCancelBTN.visibility = View.VISIBLE
                tpdProceedBTN.visibility = View.VISIBLE
            }
            "CANCELLED", "DECLINED", "COMPLETED" -> {
                tpdRemoveBTN.visibility = View.VISIBLE
            }

        }
        tpdCancelBTN.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Cancel Trade Proposal")
                .setMessage("Are you sure you want to cancel your trade proposal?")
                .setPositiveButton("Cancel trade") { dialog, _ ->
                    dialog.dismiss()
                    loadingDialog.setLoadingScreenText("Cancelling trade proposal...")
                    loadingDialog.startLoading()
                    tradeProposalActionVM.cancelTrade(
                        userID,
                        tradeProposalDetails.tp_id.toInt()
                    )
                }
                .setNegativeButton("Go back") { dialog, _ ->
                    dialog.cancel()
                }.create().show()
        }
        tpdRemoveBTN.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Remove Trade Proposal")
                .setMessage("Are you sure you want to remove this trade proposal?")
                .setPositiveButton("Remove trade") { dialog, _ ->
                    dialog.dismiss()
                    loadingDialog.setLoadingScreenText("Removing trade proposal...")
                    loadingDialog.startLoading()
                    tradeProposalActionVM.removeTrade(
                        userID,
                        tradeProposalDetails.tp_id.toInt(),
                        userEmail
                    )
                }
                .setNegativeButton("Go back") { dialog, _ ->
                    dialog.cancel()
                }.create().show()
        }
        tpdProceedBTN.setOnClickListener {
            val args = Bundle()
            args.putString("js_id", tradeProposalDetails.js_id)
            args.putString("tp_id", tradeProposalDetails.tp_id)
            val tpt = TradeProposalTransaction()
            tpt.arguments = args
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.addToBackStack(null)
            transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right)
            transaction.replace(R.id.fragment_container, tpt).commit()
        }

        initRecyclerView(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var toastMessage: String
        tradeProposalActionVM.actionResponseMLD.observe(viewLifecycleOwner) { response ->
            Log.i("ResponseReceived: ", response)
            when(response.replace("\"", "")){
                "DELETE_SUCCESS" -> {
                    toastMessage = "Successfully deleted trade proposal!"
                }
                "CANCEL_SUCCESS" -> {
                    toastMessage = "Successfully cancelled trade proposal"
                }
                "DELETE_NO_PROPOSAL_FOUND" -> {
                    toastMessage = "An unexpected error occurred. Cannot perform deleting of trade proposal.\nPlease try again later."
                }
                "CANCEL_NO_PROPOSAL_FOUND" -> {
                    toastMessage = "An unexpected error occurred. Cannot perform cancelling of trade proposal.\nPlease try again later."
                }
                else -> {
                    toastMessage = "An unexpected error occurred. Please try again later."
                }
            }
            val toast = Toast.makeText(
                requireContext(),
                toastMessage,
                Toast.LENGTH_SHORT
            )
            toast.setGravity(Gravity.CENTER, 0,0)
            toast.show()
            requireActivity().supportFragmentManager.popBackStack()
            loadingDialog.dismissLoading()
        }

        tradeProposalActionVM.ratingMLD.observe(viewLifecycleOwner) { response ->
            var toastMessage = when(response){
                101 -> "Trade proposal successfully rated!"
                102 -> "Trade proposal already rated!"
                else -> "An unexpected error occured while rating trade proposal."
            }
            loadingDialog.dismissLoading()
            Toast.makeText(
                requireContext(),
                toastMessage,
                Toast.LENGTH_SHORT
            ).show()
            tpdRatingConstraint.visibility = View.GONE
        }
    }

    private fun initRecyclerView(view: View){
        val recyclerView = view.findViewById<RecyclerView>(R.id.trade_proposal_details_item_list)
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
}