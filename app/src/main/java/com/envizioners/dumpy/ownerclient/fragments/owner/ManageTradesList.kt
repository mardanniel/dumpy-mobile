package com.envizioners.dumpy.ownerclient.fragments.owner

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.envizioners.dumpy.ownerclient.R
import com.envizioners.dumpy.ownerclient.adapter.owner.ManageTradesListViewAdapter
import com.envizioners.dumpy.ownerclient.adapter.owner.ProfileTradesListViewAdapter
import com.envizioners.dumpy.ownerclient.fragments.recycler.CreateTradeForm
import com.envizioners.dumpy.ownerclient.preference.UserPreference
import com.envizioners.dumpy.ownerclient.utils.LoadingDialog
import com.envizioners.dumpy.ownerclient.viewmodels.owner.TradesListVM
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ManageTradesList : Fragment() {

    private lateinit var tradesListRV: RecyclerView
    private lateinit var addTradeItem: Button
    private lateinit var saveTradesList: Button
    private lateinit var manageTradeListVA: ManageTradesListViewAdapter
    private lateinit var loadingDialog: LoadingDialog

    private lateinit var tradesList: MutableList<List<String>>
    private val gson = Gson()

    private val typeOne = object : TypeToken<MutableList<List<String>>>() {}.type
    private val typeTwo = object : TypeToken<MutableList<String>>() {}.type

    private val MODE_ADD = 101
    private val MODE_REPLACE = 102
    private var establishmentID: Int = 0

    private val tradesListVM: TradesListVM by lazy {
        ViewModelProvider(this).get(TradesListVM::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loadingDialog = LoadingDialog(requireActivity())

        CoroutineScope(Dispatchers.Main).launch{
            establishmentID = UserPreference.getUserBusinessID(requireContext(), "USER_BUSINESS_ID")
        }

        arguments?.apply {
            tradesList = gson.fromJson(getString("tradesList"), typeOne)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.f_o_manage_trades_list, container, false)

        tradesListRV = view.findViewById(R.id.f_o_manage_trades_list_list)
        addTradeItem = view.findViewById(R.id.f_o_add_trade_item)
        saveTradesList = view.findViewById(R.id.f_o_save_trade_items)

        saveTradesList.text = if (tradesList.size > 0){
            "Update"
        }else "Submit"

        initRecyclerView()

        addTradeItem.setOnClickListener {
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            val args = Bundle()
            args.putInt("mode", MODE_ADD)
            val atli = AddTradeListItem()
            atli.arguments = args
            transaction.addToBackStack(null)
            transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right)
            transaction.replace(R.id.fragment_container, atli).commit()
        }

//        {"0":"Bote","1":"bottle","2":"10","3":"0.5","4":"kg"}

        saveTradesList.setOnClickListener {
            if (tradesList.size > 0){
                loadingDialog.setLoadingScreenText("Updating your trades list...")
                loadingDialog.startLoading()
                tradesListVM.saveTradesList(
                    establishmentID,
                    tradesList.map { it[0] },
                    tradesList.map { it[1] },
                    tradesList.map { it[2] },
                    tradesList.map { it[3] },
                    tradesList.map { it[4] },
                )
            }else{
                Toast.makeText(
                    requireContext(),
                    "Your establishment must have at least 1 trade item to proceed.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        requireActivity().supportFragmentManager
            .setFragmentResultListener(
                "tradesListItem",
                viewLifecycleOwner
            ) { _, bundle ->
                var mode = bundle.getInt(
                    "mode"
                )
                val tradesListItem = bundle.getString(
                    "tli"
                )
                when(mode){
                    MODE_ADD -> {
                        tradesList.add(gson.fromJson(tradesListItem, typeTwo))
                        manageTradeListVA.notifyItemInserted(tradesList.size)
                    }
                    MODE_REPLACE -> {
                        val replacePos = bundle.getInt(
                            "editPos"
                        )
                        tradesList[replacePos] = gson.fromJson(tradesListItem, typeTwo)
                        manageTradeListVA.notifyItemChanged(replacePos)
                    }
                }
            }

        tradesListVM.resp.observe(viewLifecycleOwner) { response ->
            var shouldPop = false
            var toastMessage = ""
            when(response){
                101 -> {
                    toastMessage = "Successfully updated trades list!"
                    shouldPop = true
                }
                else -> {
                    toastMessage = "An unexpected error occurred. Please try again later."
                }
            }
            loadingDialog.dismissLoading()
            if (shouldPop){
                var transaction = requireActivity().supportFragmentManager
                transaction.beginTransaction().replace(R.id.fragment_container, ProfileFragment()).commitNow()
            }
            Toast.makeText(requireContext(), toastMessage, Toast.LENGTH_SHORT).show()
        }
    }

    private fun initRecyclerView(){
        val recyclerView = tradesListRV
        recyclerView.layoutManager = LinearLayoutManager(activity)
        manageTradeListVA = ManageTradesListViewAdapter(
            tradesList,
            object : ManageTradesListViewAdapter.OnManageTradeItem {
                override fun onEditClicked(position: Int) {
                    val transaction = requireActivity().supportFragmentManager.beginTransaction()
                    val args = Bundle()
                    args.putInt("mode", MODE_REPLACE)
                    args.putString("tradesListItemToBeEdit", gson.toJson(tradesList[position]))
                    args.putInt("editPos", position)
                    val atli = AddTradeListItem()
                    atli.arguments = args
                    transaction.addToBackStack(null)
                    transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right)
                    transaction.replace(R.id.fragment_container, atli).commit()
                }
            }
        )
        recyclerView.adapter = manageTradeListVA
    }
}