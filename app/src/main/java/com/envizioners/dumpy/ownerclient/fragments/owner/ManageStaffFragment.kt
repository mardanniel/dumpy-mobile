package com.envizioners.dumpy.ownerclient.fragments.owner

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.envizioners.dumpy.ownerclient.OwnerActivity
import com.envizioners.dumpy.ownerclient.R
import com.envizioners.dumpy.ownerclient.RecyclerActivity
import com.envizioners.dumpy.ownerclient.adapter.owner.ExchangeRequestViewAdapter
import com.envizioners.dumpy.ownerclient.adapter.owner.ManageStaffViewAdapter
import com.envizioners.dumpy.ownerclient.preference.UserPreference
import com.envizioners.dumpy.ownerclient.response.points.ExchangeRequest
import com.envizioners.dumpy.ownerclient.response.staff_main.StaffResultBody
import com.envizioners.dumpy.ownerclient.utils.LoadingDialog
import com.envizioners.dumpy.ownerclient.viewmodels.owner.ManageStaffVM
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ManageStaffFragment : Fragment(), ManageStaffViewAdapter.ClickListener {

    private lateinit var addStaff: Button
    private lateinit var loadingProgressBar: ProgressBar
    private lateinit var noStaffNotice: TextView
    private var junkshopOwnerID: Int? = null
    private lateinit var manageStaffViewAdapter: ManageStaffViewAdapter
    private val manageStaffVM: ManageStaffVM by lazy {
        ViewModelProvider(this).get(ManageStaffVM::class.java)
    }

    private var listOfStaff = mutableListOf<StaffResultBody>()

    private val TOGGLE_STATUS = 5
    private val REMOVE_STAFF = 6

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        CoroutineScope(Dispatchers.IO).launch {
            junkshopOwnerID = UserPreference.getUserBusinessID(requireContext(), "USER_ID")
            manageStaffVM.getStaffList(junkshopOwnerID!!)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.f_manage_staff, container, false)
        addStaff = view.findViewById(R.id.f_o_add_staff_btn)
        loadingProgressBar = view.findViewById(R.id.f_o_add_staff_loading)
        noStaffNotice = view.findViewById(R.id.f_o_no_staff_notice)
        manageStaffVM.staffListLD.observe(viewLifecycleOwner) { response ->
            if (response.isEmpty()){
                noStaffNotice.visibility = View.VISIBLE
            }
            listOfStaff = response as MutableList<StaffResultBody>
            initRecyclerView(view, listOfStaff)
            loadingProgressBar.visibility = View.GONE
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addStaff.setOnClickListener {
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.addToBackStack(null)
            transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right)
            transaction.replace(R.id.fragment_container, AddStaffFragment()).commit()
        }

        requireActivity().supportFragmentManager
            .setFragmentResultListener(
                "transaction",
                viewLifecycleOwner
            ){ _, bundle ->
                val transactionCode = bundle.getInt("TRANSACTION_CODE")
                val positionTakenCareOf = bundle.getInt("POSITION")
                when(transactionCode){
                    TOGGLE_STATUS -> {
                        listOfStaff[positionTakenCareOf].account_status = if (listOfStaff[positionTakenCareOf].account_status == "0") "1" else "0"
                        manageStaffViewAdapter.notifyItemChanged(positionTakenCareOf)
                    }
                    REMOVE_STAFF -> {
                        listOfStaff.removeAt(positionTakenCareOf)
                        manageStaffViewAdapter.notifyItemRemoved(positionTakenCareOf)
                    }
                }
            }
    }

    private fun initRecyclerView(view: View, staffList: List<StaffResultBody>){
        val recyclerView = view.findViewById<RecyclerView>(R.id.owner_staff_list)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        manageStaffViewAdapter = ManageStaffViewAdapter(staffList, this)
        recyclerView.adapter = manageStaffViewAdapter
    }

    override fun onItemClick(position: Int, staffResult: StaffResultBody) {
        val args = Bundle()
        args.putInt("currPos", position)
        args.putString("staffID", staffResult.dumpy_js_staff_id)
        args.putString("accStatus", staffResult.account_status)
        val staffDetails = StaffDetails()
        staffDetails.arguments = args
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.addToBackStack(null)
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right)
        transaction.replace(R.id.fragment_container, staffDetails).commit()
    }

    override fun onResume() {
        super.onResume()
        var currActivity = requireActivity() as OwnerActivity
        currActivity.updateActionBarTitle("Manage Staff")
    }
}