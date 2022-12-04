package com.envizioners.dumpy.ownerclient.fragments.staff

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.envizioners.dumpy.ownerclient.OwnerActivity
import com.envizioners.dumpy.ownerclient.R
import com.envizioners.dumpy.ownerclient.StaffActivity
import com.envizioners.dumpy.ownerclient.fragments.junkshop.TradeProposalsFragment
import com.envizioners.dumpy.ownerclient.preference.UserPreference
import com.envizioners.dumpy.ownerclient.utils.LoadingDialog
import com.envizioners.dumpy.ownerclient.viewmodels.staff.StaffTPActionsVM
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TradeProposalAction : Fragment() {

    // Components
    private lateinit var tradeDecision: RadioGroup
    private lateinit var tradeDecisionMsg: EditText
    private lateinit var tdSubmit: Button
    private lateinit var tdBack: Button
    private lateinit var loadingDialog: LoadingDialog

    // Decision
    private var tradeDecisionVal = "0"

    // Arguments
    private var tpID: String = ""
    private var staffID: Int = 0

    private val staffTPActionsVM: StaffTPActionsVM by lazy {
        ViewModelProvider(this).get(StaffTPActionsVM::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadingDialog = LoadingDialog(requireActivity())
            tpID = arguments?.getString("tp_id")!!
        CoroutineScope(Dispatchers.Main).launch {
            staffID = UserPreference.getUserID(requireContext(), "USER_ID")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.f_s_trade_proposal_action, container, false)

        tradeDecision = view.findViewById(R.id.f_s_decision)
        tradeDecisionMsg = view.findViewById(R.id.f_s_decision_message)
        tdSubmit = view.findViewById(R.id.f_s_submit)

        tdBack = view.findViewById(R.id.f_s_back)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var toastMessage = ""
        staffTPActionsVM._responseMLD.observe(viewLifecycleOwner) { response ->
            when(response.replace("\"", "")){
                "DECISION_SUCCESS" ->  toastMessage = "Successfully submitted your decision!"
                "DECISION_FAILED" -> toastMessage = "Failed to submit decision."
                else -> toastMessage = "An unexpected error occured.\nPlease try again later."
            }
            Toast.makeText(
                requireContext(),
                toastMessage,
                Toast.LENGTH_SHORT
            ).show()

            var transaction = requireActivity().supportFragmentManager
            transaction.popBackStack()
            transaction.popBackStack()
            transaction.beginTransaction().replace(R.id.fragment_container, TradeProposalsFragment()).commit()

            loadingDialog.dismissLoading()
        }

        tradeDecision.setOnCheckedChangeListener { _, i ->
            when(i){
                R.id.f_s_accept -> tradeDecisionVal = "0"
                R.id.f_s_deny -> tradeDecisionVal = "1"
            }
        }

        tdSubmit.setOnClickListener {
            if (!tradeDecisionMsg.text.isNullOrEmpty()){
                loadingDialog.setLoadingScreenText("Submitting decision...")
                loadingDialog.startLoading()
                staffTPActionsVM.decideTrade(
                    tpID.toInt(),
                    tradeDecisionMsg.text.toString(),
                    staffID,
                    tradeDecisionVal.toInt()
                )
            }else{
                Toast.makeText(requireContext(), "Recyclers expects feedback about their items!", Toast.LENGTH_SHORT).show()
            }
        }

        tdBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    override fun onResume() {
        super.onResume()
        var currActivity = requireActivity() as StaffActivity
        currActivity.updateActionBarTitle("Verdict")
    }

}