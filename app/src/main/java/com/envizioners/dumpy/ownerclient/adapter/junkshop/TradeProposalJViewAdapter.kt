package com.envizioners.dumpy.ownerclient.adapter.junkshop

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.envizioners.dumpy.ownerclient.R
import com.envizioners.dumpy.ownerclient.response.tradeproposal.TradeProposalResult
import com.envizioners.dumpy.ownerclient.response.tradeproposal.TradeProposalResultItem

class TradeProposalJViewAdapter(
    private val tradeProposals: TradeProposalResult,
    private val clickListener: ClickListener
): RecyclerView.Adapter<TradeProposalJViewAdapter.TradeProposalItemHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TradeProposalItemHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_j_trade_proposal_item, parent,false)
        return TradeProposalItemHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: TradeProposalItemHolder, position: Int) {

        holder.tpItems.text = tradeProposals[position].tp_items.split(",").joinToString { str ->
            str.filter { c -> c.isLetter() || c.isWhitespace() || c.isDigit() }
        }

        holder.tpDT.text = "${if (tradeProposals[position].tp_status == "PENDING") "Creation" else "Proceed"} Datetime: ${tradeProposals[position].tp_date_time}"

        if (!tradeProposals[position].coupon_name.isNullOrEmpty()){
            holder.tpCoupon.visibility = View.VISIBLE
            holder.tpCoupon.text = "Coupon: ${tradeProposals[position].coupon_name} (₱${tradeProposals[position].coupon_val})"
        }

        if (!tradeProposals[position].tp_transaction.isNullOrEmpty()){
            holder.tpPickupMethod.visibility = View.VISIBLE
            holder.tpPickupMethod.text = "Transaction Method: ${if (tradeProposals[position].tp_transaction == "0") "Pickup" else "Physical Handover"}"
        }

        Log.i("TradeProposal: ", tradeProposals[position].toString())
        holder.itemView.setOnClickListener{
            clickListener.onItemClick(tradeProposals[position])
        }
    }

    override fun getItemCount(): Int {
        return tradeProposals.size
    }

    // Recycler View Item Properties
    inner class TradeProposalItemHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var tpItems: TextView = itemView.findViewById(R.id.f_j_trade_proposal_item_title)
        var tpDT: TextView = itemView.findViewById(R.id.f_j_trade_proposal_item_action_date_time)
        var tpCoupon: TextView = itemView.findViewById(R.id.f_j_trade_proposal_coupon)
        var tpPickupMethod: TextView = itemView.findViewById(R.id.f_j_pickup_method)
    }

    // Recycler View Item onClickListener
    interface ClickListener {
        fun onItemClick(tradeProposal: TradeProposalResultItem)
    }
}