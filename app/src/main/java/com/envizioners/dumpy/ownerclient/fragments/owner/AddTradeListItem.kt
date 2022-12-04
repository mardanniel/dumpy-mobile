package com.envizioners.dumpy.ownerclient.fragments.owner

import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.envizioners.dumpy.ownerclient.R
import com.google.android.material.slider.Slider
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class AddTradeListItem : Fragment() {

    private lateinit var itemName: EditText
    private lateinit var itemType: Spinner
    private lateinit var itemPrice: EditText
    private lateinit var itemLabel: Spinner
    private lateinit var itemPointsMultiplier: Slider
    private lateinit var cancel: Button
    private lateinit var addItem: Button

    private val minMax = listOf(
        Pair(30f, 50f),
        Pair(90f, 120f),
        Pair(40f, 70f),
        Pair(110f, 150f),
        Pair(20f, 40f),
        Pair(20f, 40f),
        Pair(20f, 40f)
    )

    private val itemTypeBasis = listOf(
        "paper",
        "glass",
        "bottle",
        "metal",
        "plastic",
        "sackbag",
        "cardboardbox"
    )

    private val itemLabels = listOf(
        "kg",
        "g",
        "pcs"
    )

    private val gson = Gson()

    private var mode: Int = 0

    private var editPos: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mode = arguments?.getInt("mode")!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.f_o_add_trade_list_item, container, false)

        itemName = view.findViewById(R.id.f_o_atl_imt_item_name)
        itemType = view.findViewById(R.id.f_o_atl_item_types)
        itemPrice = view.findViewById(R.id.f_o_atl_item_price)
        itemLabel = view.findViewById(R.id.f_o_atl_item_labels)
        itemPointsMultiplier = view.findViewById(R.id.f_o_atl_points_multiplier)
        cancel = view.findViewById(R.id.f_o_atl_cancel)
        addItem = view.findViewById(R.id.f_o_atl_add_item)

        val itemTypeAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_list_item_1,
            listOf(
                "Paper / Papel",
                "Glass / Babasagin",
                "Bottle / Bote",
                "Metal / Bakal",
                "Plastic / Plastik",
                "Sack Bag / Sako",
                "Cardboard Box / Karton"
            )
        )

        val itemLabelAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_list_item_1,
            itemLabels
        )

        itemType.adapter = itemTypeAdapter
        itemLabel.adapter = itemLabelAdapter
        itemPointsMultiplier.apply {
            this.setLabelFormatter { "${it / 100}" }
        }

        itemType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                itemPointsMultiplier.apply {
                    if (mode == 101){
                        this.value = minMax[position].first
                    }
                    this.valueFrom = minMax[position].first
                    this.valueTo = minMax[position].second
                    this.stepSize = 10f
                }
            }
            override fun onNothingSelected(p0: AdapterView<*>?) = Unit
        }

        cancel.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        addItem.setOnClickListener {
            if (!itemName.text.isNullOrEmpty() && !itemPrice.text.isNullOrEmpty()){
                val transaction = requireActivity().supportFragmentManager
                transaction.setFragmentResult(
                    "tradesListItem",
                    bundleOf(
                        "mode" to mode,
                        "tli" to gson.toJson(
                            listOf(
                                itemName.text.toString(),
                                itemTypeBasis[itemType.selectedItemPosition],
                                itemPrice.text.toString(),
                                fmt(itemPointsMultiplier.value / 100),
                                itemLabels[itemLabel.selectedItemPosition]
                            )
                        ),
                        "editPos" to editPos
                    )
                )
                transaction.popBackStack()
            }else{
                Toast.makeText(
                    requireContext(),
                    "Some fields are empty! Be sure to check it out",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        checkForEditable()
    }

    private fun applyToBeEditable(tradesListItem: List<String>){
        itemName.setText(tradesListItem[0])
        val pos = itemTypeBasis.indexOf(tradesListItem[1])
        itemType.setSelection(pos)
        itemPrice.setText(tradesListItem[2])
        itemPointsMultiplier.apply {
            this.valueFrom = minMax[pos].first
            this.valueTo = minMax[pos].second
            this.stepSize = 10f
            this.value = (tradesListItem[3].toFloat() * 100f).toFloat()
            addItem.text = "Update"
        }
        itemLabel.setSelection(itemLabels.indexOf(tradesListItem[4]))
    }

    private fun checkForEditable(){
        arguments.apply {
            if (this?.get("tradesListItemToBeEdit") != null){
                editPos = this.getInt("editPos")
                val type = object : TypeToken<List<String>>() {}.type
                applyToBeEditable(gson.fromJson(this.get("tradesListItemToBeEdit").toString(), type))
            }
        }
    }

    private fun fmt(f: Float): String{
        return if(f % 1.0 != 0.0){
            String.format("%s", f);
        }else {
            String.format("%.0f", f);
        }
    }
}