package com.envizioners.dumpy.ownerclient.fragments.recycler.exchangepointsfragmentpart

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.envizioners.dumpy.ownerclient.R
import com.envizioners.dumpy.ownerclient.RecyclerActivity
import com.envizioners.dumpy.ownerclient.adapter.recycler.ExchangePointsViewAdapter
import com.envizioners.dumpy.ownerclient.preference.UserPreference
import com.envizioners.dumpy.ownerclient.response.points.PointsDetail
import com.envizioners.dumpy.ownerclient.utils.LoadingDialog
import com.envizioners.dumpy.ownerclient.viewmodels.recycler.ExchangePointsVM
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ExchangePointsList : Fragment(), ExchangePointsViewAdapter.OnButtonClicked{

    private lateinit var exchangePointsViewAdapter: ExchangePointsViewAdapter
    private lateinit var emptyListNotice: TextView
    private lateinit var overallPoints: TextView
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var listOfPoints: MutableList<PointsDetail>

    private var tobePopped: Int? = null

    private val exchangePointsVM: ExchangePointsVM by lazy {
        ViewModelProvider(this).get(ExchangePointsVM::class.java)
    }
    private var userID: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadingDialog = LoadingDialog(requireActivity())
        loadingDialog.setLoadingScreenText("Loading your acquired points...")
        loadingDialog.startLoading()
        CoroutineScope(Dispatchers.IO).launch {
            userID = UserPreference.getUserID(requireContext(), "USER_ID")
            exchangePointsVM.getExchangePointsList(userID)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val exchangePointsView = inflater.inflate(R.layout.f_r_exchange_points_list, container, false)
        emptyListNotice = exchangePointsView.findViewById(R.id.f_r_tp_empty_exchange_points_list_text)
        overallPoints = exchangePointsView.findViewById(R.id.f_r_ep_oap)
        exchangePointsVM.exchangePointsMLD.observe(viewLifecycleOwner) { response ->
            overallPoints.text = "Overall accumulated points:\n${response.current_pts ?: "0"} pts"
            listOfPoints = response.points_details as MutableList<PointsDetail>
            initRecyclerView(exchangePointsView, listOfPoints)
            loadingDialog.dismissLoading()
        }
        return exchangePointsView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        exchangePointsVM.sendRequestResponse.observe(viewLifecycleOwner) { response ->
            var shouldPop = false
            var toastMessage: String = ""
            when(response){
                101 -> {
                    toastMessage = "Successfully sent your request!"
                    shouldPop = true
                }
                104 -> {
                    toastMessage = "You currently don't have the minimum amount of points (which is 500 pts) to exchange points."
                }
                else -> {
                    toastMessage = "An unexpected error occured. Please try again later"
                }
            }
            Toast.makeText(requireContext(), toastMessage, Toast.LENGTH_SHORT).show()
            if(shouldPop){
                if (tobePopped != null) {
                    listOfPoints.removeAt(tobePopped!!)
                    exchangePointsViewAdapter.notifyItemRemoved(tobePopped!!)
                    if (listOfPoints.size == 0){
                        emptyListNotice.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun initRecyclerView(view: View, exchangePointsList: List<PointsDetail>){
        val recyclerView = view.findViewById<RecyclerView>(R.id.f_r_exchange_points_rv)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        emptyListNotice.visibility = if(exchangePointsList.isNotEmpty()) View.GONE else View.VISIBLE
        exchangePointsViewAdapter = ExchangePointsViewAdapter(
            exchangePointsList,
            this
        )
        recyclerView.adapter = exchangePointsViewAdapter
    }

    override fun onButtonClicked(position: Int) {
        AlertDialog.Builder(requireContext())
            .setTitle("Exchange Points")
            .setMessage("Are you sure you want to want to exchange your points?")
            .setPositiveButton("Exchange points") { dialog, _ ->
                loadingDialog.setLoadingScreenText("Sending your request...")
                loadingDialog.dismissLoading()
                exchangePointsVM.sendRequest(
                    userID,
                    listOfPoints[position].js_id.toInt()
                )
                tobePopped = position
                dialog.dismiss()
            }
            .setNegativeButton("Go back") { dialog, _ ->
                dialog.cancel()
            }.create().show()
    }

    override fun onResume() {
        super.onResume()
        var currActivity = requireActivity() as RecyclerActivity
        currActivity.updateActionBarTitle("Exchange Points")
    }
}