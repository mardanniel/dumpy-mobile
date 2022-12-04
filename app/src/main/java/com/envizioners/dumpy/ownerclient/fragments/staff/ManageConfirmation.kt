package com.envizioners.dumpy.ownerclient.fragments.staff

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.envizioners.dumpy.ownerclient.OwnerActivity
import com.envizioners.dumpy.ownerclient.R
import com.envizioners.dumpy.ownerclient.StaffActivity
import com.envizioners.dumpy.ownerclient.adapter.staff.ManageConfirmationViewAdapter
import com.envizioners.dumpy.ownerclient.adapter.staff.ManageConfirmationViewAdapter.OnEditTextChanged
import com.envizioners.dumpy.ownerclient.preference.UserPreference
import com.envizioners.dumpy.ownerclient.response.tradeproposal.TradeProposalResultItem
import com.envizioners.dumpy.ownerclient.response.tradeproposal.TradesLists
import com.envizioners.dumpy.ownerclient.utils.LoadingDialog
import com.envizioners.dumpy.ownerclient.viewmodels.shared.TradesListVM
import com.envizioners.dumpy.ownerclient.viewmodels.staff.StaffTPActionsVM
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

class ManageConfirmation : Fragment() {

    // Components
    private lateinit var manageConfirmationViewAdapter: ManageConfirmationViewAdapter
    private lateinit var recyclerName: TextView
    private lateinit var proceedDT: TextView
    private lateinit var coupon: TextView
    private lateinit var userAddress: TextView
    private lateinit var tradeTransaction: TextView
    private lateinit var mainList: RecyclerView
    private lateinit var cancelBtn: Button
    private lateinit var submitBtn: Button
    private lateinit var loadingProgressBar: ProgressBar
    private lateinit var loadingDialog: LoadingDialog

    // Data Transformation Tool
    private lateinit var gson: Gson

    // Arguments
    private lateinit var tradeProposalDetails: TradeProposalResultItem

    // Details
    private var userID: Int = 0
    private var userEmail: String = ""
    private lateinit var tradeItems: List<String>
    private lateinit var tradeQuantities: MutableList<String>
    private lateinit var tradeLabels: List<String>
    private lateinit var tradeTypes: List<String>
    private lateinit var tradeImageNames: List<String>

    private val tradesListVM: TradesListVM by lazy {
        ViewModelProvider(this).get(TradesListVM::class.java)
    }

    private val staffTPActionsVM: StaffTPActionsVM by lazy {
        ViewModelProvider(this).get(StaffTPActionsVM::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CoroutineScope(Dispatchers.Main).launch {
            val establishmentID = UserPreference.getUserBusinessID(requireContext(), "USER_BUSINESS_ID")
            tradesListVM.getTradesList(establishmentID)
        }
        gson = Gson()
        tradeProposalDetails = arguments?.getSerializable("TP_ITEM_MC") as TradeProposalResultItem
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
        val view =  inflater.inflate(R.layout.f_s_manage_confirmation, container, false)

        recyclerName = view.findViewById(R.id.f_s_manage_confirmation_recycler_name)
        proceedDT = view.findViewById(R.id.f_s_manage_confirmation_recycler_proceed_dt)
        coupon = view.findViewById(R.id.f_s_manage_confirmation_recycler_coupon)
        userAddress = view.findViewById(R.id.f_s_manage_confirmation_recycler_location)
        tradeTransaction = view.findViewById(R.id.f_s_manage_confirmation_trade_transaction)
        mainList = view.findViewById(R.id.f_s_manage_confirmation_list)
        cancelBtn = view.findViewById(R.id.f_s_manage_confirmation_cancel)
        submitBtn = view.findViewById(R.id.f_s_manage_confirmation_submit)
        loadingProgressBar = view.findViewById(R.id.f_s_manage_confirmation_loading)
        loadingDialog = LoadingDialog(requireActivity())

        recyclerName.text = "${tradeProposalDetails.user_fname} ${tradeProposalDetails.user_mname} ${tradeProposalDetails.user_lname}"
        proceedDT.text = "Proceed Datetime: ${tradeProposalDetails.tp_date_time}"
        coupon.apply {
            if (tradeProposalDetails.coupon_name != null){
                this.visibility = View.VISIBLE
                this.text = "Coupon: ${tradeProposalDetails.coupon_name} (₱${tradeProposalDetails.coupon_val})"
            }
        }

        userAddress.apply {
            if (tradeProposalDetails.tp_transaction != null){
                if (tradeProposalDetails.tp_transaction?.toInt() == 0){
                    this.visibility = View.VISIBLE
                    this.text = "Recycler Address: \"${tradeProposalDetails.user_address}\""
                }
            }
        }

        tradeTransaction.apply {
            if (tradeProposalDetails.tp_transaction != null){
                this.visibility = View.VISIBLE
                this.text = if (tradeProposalDetails.tp_transaction?.toInt() == 0){
                    "Transaction via Pickup"
                }else{
                    "Transaction via Physical Handover"
                }
            }
        }
        cancelBtn.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        submitBtn.setOnClickListener {
            loadingDialog.setLoadingScreenText("Concluding trade proposal...")
            loadingDialog.startLoading()
            val listInt = mutableListOf<Int>()
            tradeQuantities.forEach {
                listInt.add(it.toInt())
            }
            Log.i("ListInt", listInt.toString())
            staffTPActionsVM.concludeTrade(
                tradeProposalDetails.tp_id.toInt(),
                listInt
            )
        }
        staffTPActionsVM._responseMLD.observe(viewLifecycleOwner) { response ->
            var toastMessage = when(response.replace("\"", "")){
                "CONCLUDE_SUCCESS" ->{
                    "Successfully concluded trade proposal!"
                }
                "CONCLUDE_FAILED_TRADE_IS_COMPLETED" -> {
                    "Trade Proposal already concluded!"
                }
                "CONCLUDE_FAILED_TRADE_IS_ALREADY_PROCESSING" -> {
                    "Trade Proposal currently being concluded!"
                }
                else -> {
                    "Failed to conclude trade proposal. Please try again later."
                }
            }

            var sfm = requireActivity().supportFragmentManager
            sfm.setFragmentResult(
                "doReload",
                bundleOf(
                    "shouldReload" to true
                )
            )
            sfm.popBackStack()
            sfm.beginTransaction().replace(R.id.fragment_container, ConfirmationsQueue()).commit()
            loadingDialog.dismissLoading()
            Toast.makeText(requireContext(), toastMessage, Toast.LENGTH_SHORT).show()
        }
        tradesListVM.tradesListLD.observe(viewLifecycleOwner) { response ->
            initRecyclerView(response)
            loadingProgressBar.visibility = View.GONE
        }
        return view
    }

    private fun initRecyclerView(tradesLists: TradesLists){
        mainList.layoutManager = LinearLayoutManager(activity)
        manageConfirmationViewAdapter = ManageConfirmationViewAdapter(
            tradeItems,
            tradeQuantities,
            tradeLabels,
            tradeTypes,
            tradeImageNames,
            userEmail,
            tradesLists,
            requireContext(),
            object : OnEditTextChanged {
                override fun onTextChanged(pos: Int, str: CharSequence?) {
                    tradeQuantities[pos] = str.toString()
                    Log.i("Pos: ", pos.toString())
                    Log.i("PosVal: ", str.toString())
                }
            }
        )
        mainList.adapter = manageConfirmationViewAdapter
    }

    override fun onResume() {
        super.onResume()
        var currActivity = requireActivity() as StaffActivity
        currActivity.updateActionBarTitle("Manage Confirmation")
    }
}