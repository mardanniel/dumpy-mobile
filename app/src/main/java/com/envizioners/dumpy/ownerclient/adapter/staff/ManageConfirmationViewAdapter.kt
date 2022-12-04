package com.envizioners.dumpy.ownerclient.adapter.staff

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.envizioners.dumpy.ownerclient.R
import com.envizioners.dumpy.ownerclient.response.tradeproposal.TradesLists
import kotlin.math.floor

class ManageConfirmationViewAdapter: RecyclerView.Adapter<ManageConfirmationViewAdapter.ManageConfirmationItemHolder> {

    private val tradeItems: List<String>
    private val tradeQuantities: List<String>
    private val tradeLabels: List<String>
    private val tradeTypes: List<String>
    private val tradeImages: List<String>
    private val userEmail: String
    private val tradesList: TradesLists
    private var context: Context
    private var onEditTextChanged: OnEditTextChanged

    constructor(
        tradeItems: List<String>,
        tradeQuantities: List<String>,
        tradeLabels: List<String>,
        tradeTypes: List<String>,
        tradeImages: List<String>,
        userEmail: String,
        tradesList: TradesLists,
        context: Context,
        onEditTextChanged: OnEditTextChanged
    ) : super() {
        this.tradeItems = tradeItems
        this.tradeQuantities = tradeQuantities
        this.tradeLabels = tradeLabels
        this.tradeTypes = tradeTypes
        this.tradeImages = tradeImages
        this.userEmail = userEmail
        this.tradesList = tradesList
        this.context = context
        this.onEditTextChanged = onEditTextChanged
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ManageConfirmationItemHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_manage_confirmation_item, parent, false)
        return ManageConfirmationItemHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ManageConfirmationItemHolder, position: Int) {
        holder.itemName.text = "${tradeItems[position]} (${tradeTypes[position]})"

        /**
         * total price =Quantity x Item Price (from trade list)
         * points = total price x multiplier
         *
         * Sample: [["Bakal","metal","10","1.2","kg"],["Bote","glass","3","0.9","pcs"],["Tanso","metal","150","1.5","kg"]]
         */

        var itemPrice = 0
        var itemPoints = 0.0
        tradesList.forEach {
            if(tradeItems[position] == it[0]){
                itemPrice = tradeQuantities[position].toInt() * it[2].toInt()
                itemPoints = itemPrice * it[3].toDouble()
            }
        }

        holder.itemPricePoints.text = "₱${itemPrice} (${floor(itemPoints)} pts)"
        holder.itemQuantity.text = Editable.Factory.getInstance().newEditable(tradeQuantities[position])
        holder.itemQuantity.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit
            override fun afterTextChanged(p0: Editable?) = Unit

            override fun onTextChanged(input: CharSequence?, p1: Int, p2: Int, p3: Int) {
                onEditTextChanged.onTextChanged(position, input)
            }

        })
        holder.itemQuantityLabel.text = tradeLabels[position]
    }

    override fun getItemCount(): Int = tradeItems.size

    interface OnEditTextChanged {
        fun onTextChanged(pos: Int, str: CharSequence?)
    }

    inner class ManageConfirmationItemHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val itemName: TextView = itemView.findViewById(R.id.f_s_mci_item)
        val itemPricePoints: TextView = itemView.findViewById(R.id.f_s_mci_item_price_and_point)
        val itemQuantity: EditText = itemView.findViewById(R.id.f_s_mci_item_quantity)
        val itemQuantityLabel: TextView = itemView.findViewById(R.id.f_s_mci_item_quantity_label)
    }
}