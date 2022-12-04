package com.envizioners.dumpy.ownerclient.fragments.recycler.exchangepointsfragmentpart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.envizioners.dumpy.ownerclient.R
import com.envizioners.dumpy.ownerclient.RecyclerActivity
import com.envizioners.dumpy.ownerclient.adapter.recycler.CouponsViewAdapter
import com.envizioners.dumpy.ownerclient.preference.UserPreference
import com.envizioners.dumpy.ownerclient.response.points.CouponDetail
import com.envizioners.dumpy.ownerclient.utils.LoadingDialog
import com.envizioners.dumpy.ownerclient.viewmodels.recycler.ExchangePointsVM
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CouponsList: Fragment() {

    private lateinit var couponsViewAdapter: CouponsViewAdapter
    private lateinit var emptyListNotice: TextView
    private lateinit var loadingDialog: LoadingDialog
    private val exchangePointsVM: ExchangePointsVM by lazy {
        ViewModelProvider(this).get(ExchangePointsVM::class.java)
    }
    private var userID: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadingDialog = LoadingDialog(requireActivity())
        loadingDialog.setLoadingScreenText("Loading your coupons...")
        loadingDialog.startLoading()
        CoroutineScope(Dispatchers.IO).launch {
            userID = UserPreference.getUserID(requireContext(), "USER_ID")
            exchangePointsVM.getCouponsList(userID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var couponsView = inflater.inflate(R.layout.f_r_coupons_list, container, false)
        emptyListNotice = couponsView.findViewById(R.id.f_r_tp_empty_coupons_list_text)
        exchangePointsVM.couponsMLD.observe(viewLifecycleOwner) { response ->
            initRecyclerView(couponsView, response)
            loadingDialog.dismissLoading()
        }
        return couponsView
    }

    private fun initRecyclerView(view: View, couponsList: List<CouponDetail>){
        val recyclerView = view.findViewById<RecyclerView>(R.id.f_r_coupons_rv)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        emptyListNotice.visibility = if(couponsList.isNotEmpty()) View.GONE else View.VISIBLE
        couponsViewAdapter = CouponsViewAdapter(couponsList)
        recyclerView.adapter = couponsViewAdapter
    }

    override fun onResume() {
        super.onResume()
        var currActivity = requireActivity() as RecyclerActivity
        currActivity.updateActionBarTitle("Coupons List")
    }
}