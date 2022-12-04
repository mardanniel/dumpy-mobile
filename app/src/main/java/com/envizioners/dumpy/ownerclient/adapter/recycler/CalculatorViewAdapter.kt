package com.envizioners.dumpy.ownerclient.adapter.recycler

import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.envizioners.dumpy.ownerclient.R

class CalculatorViewAdapter
    : RecyclerView.Adapter<CalculatorViewAdapter.CalculatorItemHolder>{

    private val tradesList: List<List<String>>
    private var onEditTextChanged: OnEditTextChanged

    constructor(
        tradesList: List<List<String>>,
        onEditTextChanged: OnEditTextChanged
    ) : super() {
        this.tradesList = tradesList
        this.onEditTextChanged = onEditTextChanged
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalculatorItemHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_calculator_item, parent, false)
        return CalculatorItemHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CalculatorItemHolder, position: Int) {

        /**
         * total price =Quantity x Item Price (from trade list)
         * points = total price x multiplier
         *
         * Sample:
         * [
         * ["Bakal","metal","10","1.2","kg"],
         * ["Bote","glass","3","0.9","pcs"],
         * ["Tanso","metal","150","1.5","kg"]
         * ]
         */

        holder.itemName.text = "${tradesList[position][0]} (${tradesList[position][1]})"
        holder.itemppq.text = "₱${tradesList[position][2]} per ${tradesList[position][4]}"
        holder.itemQuantity.text = Editable.Factory.getInstance().newEditable("0")

        holder.itemQuantity.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit
            override fun afterTextChanged(p0: Editable?) = Unit

            override fun onTextChanged(input: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (input.isNullOrEmpty()){
                    onEditTextChanged.onTextChanged(position, "0")
                }else {
                    onEditTextChanged.onTextChanged(position, input)
                }
            }

        })
    }

    override fun getItemCount(): Int = tradesList.size

    interface OnEditTextChanged {
        fun onTextChanged(pos: Int, quantity: CharSequence?)
    }

    inner class CalculatorItemHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val itemName: TextView = itemView.findViewById(R.id.f_r_calc_item_name)
        val itemppq: TextView = itemView.findViewById(R.id.f_r_calc_item_ppq)
        val itemQuantity: EditText = itemView.findViewById(R.id.f_r_calc_quantity)
    }
}