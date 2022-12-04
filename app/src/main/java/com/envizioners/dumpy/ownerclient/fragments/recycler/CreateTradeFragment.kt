package com.envizioners.dumpy.ownerclient.fragments.recycler

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.envizioners.dumpy.ownerclient.R
import com.envizioners.dumpy.ownerclient.RecyclerActivity
import com.envizioners.dumpy.ownerclient.adapter.recycler.CreateTradeViewAdapter
import com.envizioners.dumpy.ownerclient.fragments.shared.DashboardFragment
import com.envizioners.dumpy.ownerclient.preference.UserPreference
import com.envizioners.dumpy.ownerclient.response.tradeproposal.CreateTradeItem
import com.envizioners.dumpy.ownerclient.utils.LoadingDialog
import com.envizioners.dumpy.ownerclient.viewmodels.recycler.CreateTradeVM
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CreateTradeFragment : Fragment() {

    private val createTradeVM: CreateTradeVM by lazy {
        ViewModelProvider(this).get(CreateTradeVM::class.java)
    }

    private lateinit var tradeListNotice: TextView
    private lateinit var loadingDialog: LoadingDialog

    private lateinit var createTradeViewAdapter: CreateTradeViewAdapter
    private val createTradeItems = mutableListOf<CreateTradeItem>()
    private lateinit var tradesListString: String
    private lateinit var tradesList: List<List<String>>
    private lateinit var junkshopID: String
    private var userID: Int = 0
    private lateinit var userEmail: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CoroutineScope(Dispatchers.IO).launch {
            userID = UserPreference.getUserID(requireContext(), "USER_ID")
            userEmail = UserPreference.getUserEmail(requireContext(), "USER_EMAIL")
        }
        tradesListString = arguments?.getString("tradesList")!!
        junkshopID = arguments?.getString("junkshopID")!!
        var gson = Gson()
        val type = object : TypeToken<List<List<String>>>() {}.type
        tradesList = gson.fromJson(tradesListString, type)
        Log.i("Parcel (NEVA): ", tradesList.toString())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.f_r_create_trade, container, false)
        tradeListNotice = view.findViewById(R.id.f_r_create_trade_notice)
        val recyclerView = view.findViewById<RecyclerView>(R.id.f_r_create_trade_item_trades_list)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        createTradeViewAdapter = CreateTradeViewAdapter(createTradeItems)
        recyclerView.adapter = createTradeViewAdapter
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        var currActivity = requireActivity() as RecyclerActivity
        currActivity.updateActionBarTitle("Create Trade Proposal")
        loadingDialog = LoadingDialog(requireActivity())
        val addItemButton = view.findViewById<Button>(R.id.f_r_create_trade_add_item)
        val submitButton = view.findViewById<Button>(R.id.f_r_create_trade_submit)
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        addItemButton.setOnClickListener {
            val args = Bundle()
            args.putString("tradesList", tradesListString)
            val ctform = CreateTradeForm()
            ctform.arguments = args
            transaction?.addToBackStack(null)
            transaction?.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right)
            transaction?.replace(R.id.fragment_container, ctform)?.commit()
        }
        submitButton.setOnClickListener {
            if(createTradeItems.size > 0){
                loadingDialog.setLoadingScreenText(
                    "Submitting your trade proposal..."
                )
                loadingDialog.startLoading()
                createTradeVM.insertTrade(
                    createTradeItems,
                    junkshopID.toInt(),
                    userEmail,
                    userID
                )
                it.isEnabled = false
            }else{
                Toast
                    .makeText(
                        requireContext(),
                        "Looks like you haven't added an item to cart!",
                        Toast.LENGTH_SHORT
                    )
                    .show()
            }
        }

        requireActivity()
            .supportFragmentManager
            .setFragmentResultListener(
                "create_trade_item",
                viewLifecycleOwner
            ) { _, bundle ->
            val createTradeItemFromForm = bundle.getSerializable(
                "create_trade_form_result"
            ) as CreateTradeItem
            addItem(createTradeItemFromForm)
            tradeListNotice.visibility = View.GONE
        }

        createTradeVM.recyclerUploadLD.observe(viewLifecycleOwner){
            if (it.result == "SUCCESS"){
                AlertDialog.Builder(requireContext())
                    .setView(requireActivity().layoutInflater.inflate(R.layout.success_dialog, null))
                    .setPositiveButton("Go Back to Dashboard") { dialog, _ ->
                        dialog.dismiss()
                        requireActivity().supportFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, DashboardFragment()).commit()
                        submitButton.isEnabled = true
                    }.create().show()
            }else{
                Toast.makeText(requireContext(), "An unexpected error occurred. Please try again!", Toast.LENGTH_SHORT).show()
            }
            loadingDialog.dismissLoading()
        }
    }

    private fun addItem(createTradeItem: CreateTradeItem){
        createTradeItems.add(createTradeItem)
        createTradeViewAdapter.notifyDataSetChanged()
    }
}