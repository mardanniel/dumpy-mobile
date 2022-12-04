package com.envizioners.dumpy.ownerclient.fragments.recycler.tradeproposalsfragmentpart

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.ViewModelProvider
import com.envizioners.dumpy.ownerclient.R
import com.envizioners.dumpy.ownerclient.preference.UserPreference
import com.envizioners.dumpy.ownerclient.utils.FragmentUtils
import com.envizioners.dumpy.ownerclient.utils.LoadingDialog
import com.envizioners.dumpy.ownerclient.viewmodels.recycler.RecyclerTradeProposalActionVM
import com.envizioners.dumpy.ownerclient.viewmodels.recycler.SpecificCouponsVM
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TradeProposalTransaction : Fragment() {

    private lateinit var ttTransactionOption: RadioGroup
    private lateinit var ttCouponsHeader: TextView
    private lateinit var ttCoupons: Spinner
    private lateinit var ttBack: Button
    private lateinit var ttSubmit: Button
    private lateinit var loadingDialog: LoadingDialog

    private var ttTransaction = "1"
    private var ttCoupon: String? = null
    private var userID: Int = 0
    private var js_id = ""
    private var tp_id = ""

    private val specificCouponsVM: SpecificCouponsVM by lazy {
        ViewModelProvider(this).get(SpecificCouponsVM::class.java)
    }

    private val tradeProposalAction: RecyclerTradeProposalActionVM by lazy {
        ViewModelProvider(this).get(RecyclerTradeProposalActionVM::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadingDialog = LoadingDialog(requireActivity())
        loadingDialog.setLoadingScreenText("Processing...")
        loadingDialog.startLoading()
        js_id = arguments?.getString("js_id")!!

        arguments.apply {
            js_id = this?.getString("js_id")!!
            tp_id = this.getString("tp_id")!!
        }
        CoroutineScope(Dispatchers.Main).launch {
            userID = UserPreference.getUserID(requireContext(), "USER_ID")
            specificCouponsVM.getSpecificCoupons(userID, js_id.toInt())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.f_r_trade_proposal_transact, container, false)
        ttTransactionOption = view.findViewById(R.id.tt_options)
        ttCouponsHeader = view.findViewById(R.id.tt_coupons_header)
        ttCoupons = view.findViewById(R.id.tt_coupons)
        ttBack = view.findViewById(R.id.tt_back_btn)
        ttSubmit = view.findViewById(R.id.tt_submit_btn)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        specificCouponsVM.scLD.observe(viewLifecycleOwner) { response ->

            if (response.size > 0){

                // RadioGroup (Transaction Type)
                ttTransactionOption.setOnCheckedChangeListener { _, i ->
                    when(i){
                        R.id.tt_pickup -> ttTransaction = "0"
                        R.id.tt_physical -> ttTransaction = "1"
                    }
                }

                val coupons = mutableListOf<String?>(null)
                var responseList = mutableListOf("Choose Coupon")
                response.forEach {
                    coupons.add(it.coupon_name)
                    responseList.add("${it.coupon_name} - ₱${it.coupon_price}")
                }

                // Spinner (Coupon - if exist)
                val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
                    requireContext(),
                    android.R.layout.simple_list_item_1,
                    responseList
                )

                // Spinner (Coupon) Adapter
                ttCoupons.adapter = adapter

                // Spinner Listener
                ttCoupons.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                        ttCoupon = coupons[pos]
                    }
                    override fun onNothingSelected(p0: AdapterView<*>?) = Unit
                }
            }else{
                ttCoupons.visibility = View.GONE
                ttCouponsHeader.visibility = View.GONE
            }

            // Submit
            ttSubmit.setOnClickListener {
                loadingDialog.setLoadingScreenText("Processing...")
                loadingDialog.startLoading()
                tradeProposalAction.proceedTrade(
                    userID,
                    tp_id.toInt(),
                    js_id.toInt(),
                    ttTransaction,
                    ttCoupon
                )
            }

            // Back
            ttBack.setOnClickListener {
                requireActivity().supportFragmentManager.popBackStack()
            }

            loadingDialog.dismissLoading()
        }
        var toastMessage: String
        tradeProposalAction.actionResponseMLD.observe(viewLifecycleOwner) { response ->
            Log.i("ResponseReceived: ", response)
            toastMessage = when(response.replace("\"", "")){
                "PROCEED_SUCCESS:WCOUPON" -> {
                    "Successfully proceeded transaction with your coupon!"
                }
                "PROCEED_SUCCESS:WOCOUPON" -> {
                    "Successfully proceeded transaction!"
                }
                else -> {
                    "An unexpected error occurred. Please try again later."
                }
            }
            val toast = Toast.makeText(
                requireContext(),
                toastMessage,
                Toast.LENGTH_SHORT
            )
            toast.setGravity(Gravity.CENTER, 0,0)
            toast.show()
            val fm = requireActivity().supportFragmentManager
            fm.popBackStack()
            fm.popBackStack()
            loadingDialog.dismissLoading()
        }
    }
}