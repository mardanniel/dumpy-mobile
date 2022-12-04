package com.envizioners.dumpy.ownerclient.fragments.recycler

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.envizioners.dumpy.ownerclient.R
import com.envizioners.dumpy.ownerclient.RecyclerActivity
import com.envizioners.dumpy.ownerclient.adapter.recycler.CalculatorViewAdapter
import com.envizioners.dumpy.ownerclient.adapter.staff.ManageConfirmationViewAdapter
import com.envizioners.dumpy.ownerclient.response.tradeproposal.TradesLists
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class CalculatorFragment : Fragment() {

    private lateinit var calcVA: CalculatorViewAdapter
    private lateinit var calcTotalPrice: TextView
    private lateinit var calcTotalPoints: TextView
    private lateinit var calcList: RecyclerView

    private lateinit var tradesListString: String
    private lateinit var tradesList: List<List<String>>
    private lateinit var junkshopID: String

    private lateinit var tradeQuantityChanges: MutableList<Int>

    private lateinit var itemPrices: MutableList<Double>
    private lateinit var itemPoints: MutableList<Double>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tradesListString = arguments?.getString("tradesList")!!
        junkshopID = arguments?.getString("junkshopID")!!
        var gson = Gson()
        val type = object : TypeToken<List<List<String>>>() {}.type
        tradesList = gson.fromJson(tradesListString, type)
        Log.i("Parcel (NEVA): ", tradesList.toString())

        tradeQuantityChanges = MutableList<Int>(tradesList.size) { 0 }
        itemPrices = MutableList<Double>(tradesList.size){ 0.0 }
        itemPoints = MutableList<Double>(tradesList.size) { 0.0 }

    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.f_r_calculator, container, false)

        calcList = view.findViewById(R.id.f_r_calc_list)
        calcTotalPrice = view.findViewById(R.id.f_r_calc_total_price)
        calcTotalPoints = view.findViewById(R.id.f_r_calc_total_points)

        calcTotalPrice.text = "₱0"
        calcTotalPoints.text = "0pts"

        initRecyclerView(tradesList)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        var currActivity = requireActivity() as RecyclerActivity
        currActivity.updateActionBarTitle("Calculator")
    }

    private fun initRecyclerView(tradesLists: List<List<String>>){
        calcList.layoutManager = LinearLayoutManager(activity)
        calcVA = CalculatorViewAdapter(
            tradesLists,
            object : CalculatorViewAdapter.OnEditTextChanged {
                @SuppressLint("SetTextI18n")
                override fun onTextChanged(pos: Int, quantity: CharSequence?) {
                    var tp = calculateTotalPrice(pos, quantity?.toString()!!.toInt())
                    calcTotalPrice.text = "₱${tp}"
                    calcTotalPoints.text = "${calculateTotalPoints()}pts"

                    Log.i("ITEMPR", itemPrices.toString())
                    Log.i("ITEMPO", itemPoints.toString())
                }
            }
        )
        calcList.adapter = calcVA
    }

    private fun calculateTotalPrice(pos: Int, quantity: Int): Double{
        // Change quantity on pos
        tradeQuantityChanges[pos] = quantity

        // Change Prices
        itemPrices[pos] = (tradeQuantityChanges[pos] * tradesList[pos][2].toInt()).toDouble()

        // Change Points
        itemPoints[pos] = itemPrices[pos] * tradesList[pos][3].toDouble()

        return (itemPrices.sum())
    }

    private fun calculateTotalPoints(): Double {
        return itemPoints.sum()
    }
}