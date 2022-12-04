package com.envizioners.dumpy.ownerclient.fragments.owner

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.envizioners.dumpy.ownerclient.R
import com.envizioners.dumpy.ownerclient.adapter.owner.ExchangeRequestViewAdapter
import com.envizioners.dumpy.ownerclient.preference.UserPreference
import com.envizioners.dumpy.ownerclient.response.points.ExchangeRequest
import com.envizioners.dumpy.ownerclient.utils.LoadingDialog
import com.envizioners.dumpy.ownerclient.viewmodels.owner.ExchangeRequestListVM
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ExchangeRequestFragment : Fragment(), ExchangeRequestViewAdapter.OnButtonClicked {

    private lateinit var exchangeRequestViewAdapter: ExchangeRequestViewAdapter
    private lateinit var exchangeReqNotice: TextView
    private lateinit var loadingProgressBar: ProgressBar
    private lateinit var loadingDialog: LoadingDialog

    private var toBePopped: Int? = null

    private lateinit var mainList: MutableList<ExchangeRequest>
    private val erlVM: ExchangeRequestListVM by lazy {
        ViewModelProvider(this).get(ExchangeRequestListVM::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadingDialog = LoadingDialog(requireActivity())
        CoroutineScope(Dispatchers.Main).launch {
            val establishmentID = UserPreference.getUserBusinessID(requireContext(), "USER_BUSINESS_ID")
            erlVM.getRequestList(establishmentID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val exchangeRequestView = inflater.inflate(R.layout.f_o_exchange_request, container, false)

        exchangeReqNotice = exchangeRequestView.findViewById(R.id.f_o_no_exchange_requests)
        loadingProgressBar = exchangeRequestView.findViewById(R.id.f_o_exchange_requests_loading)

        erlVM.erlMLD.observe(viewLifecycleOwner) { response ->
            if (response.isEmpty()){
                exchangeReqNotice.visibility = View.VISIBLE
            }
            loadingProgressBar.visibility = View.GONE
            mainList = response as MutableList<ExchangeRequest>
            initRecyclerView(exchangeRequestView, mainList)
        }

        return exchangeRequestView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        erlVM.acceptER.observe(viewLifecycleOwner) { response ->
            var shouldPop = false
            var toastMessage = ""
            when(response){
                101 -> {
                    toastMessage = "Successfully accepted exchange request!"
                    shouldPop = true
                }
                else -> {
                    toastMessage = "An unexpected error occurred. Please try again later."
                }
            }
            Toast.makeText(
                requireContext(),
                toastMessage,
                Toast.LENGTH_SHORT
            ).show()
            if (shouldPop){
                if (toBePopped != null){
                    mainList.removeAt(toBePopped!!)
                    exchangeRequestViewAdapter.notifyItemRemoved(toBePopped!!)
                    if (mainList.size == 0) {
                        exchangeReqNotice.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun initRecyclerView(view: View, exchangeRequestList: List<ExchangeRequest>){
        val recyclerView = view.findViewById<RecyclerView>(R.id.f_o_exchange_request_rv)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        exchangeRequestViewAdapter = ExchangeRequestViewAdapter(exchangeRequestList, this)
        recyclerView.adapter = exchangeRequestViewAdapter
    }

    override fun onButtonClicked(pos: Int, requestID: Int) {
        AlertDialog.Builder(requireContext())
            .setTitle("Exchange Request")
            .setMessage("Are you sure you want to want to accept this request?")
            .setPositiveButton("Exchange points") { dialog, _ ->
                loadingDialog.setLoadingScreenText("Accepting recycler exchange request...")
                loadingDialog.dismissLoading()
                erlVM.acceptExchangeRequest(
                    requestID
                )
                dialog.dismiss()
                toBePopped = pos
            }
            .setNegativeButton("Go back") { dialog, _ ->
                dialog.cancel()
            }.create().show()
    }
}