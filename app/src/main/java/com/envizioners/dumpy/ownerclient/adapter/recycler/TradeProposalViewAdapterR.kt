package com.envizioners.dumpy.ownerclient.adapter.recycler

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.envizioners.dumpy.ownerclient.R
import com.envizioners.dumpy.ownerclient.response.recycler_main.TradeProposalsRResultBody

class TradeProposalViewAdapterR(
    val tradeProposals: List<TradeProposalsRResultBody>,
    val cardColor: String,
    val clickListener: ClickListener
): RecyclerView.Adapter<TradeProposalViewAdapterR.TradeProposalItemHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TradeProposalItemHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_r_trade_proposal_item, parent,false)
        return TradeProposalItemHolder(view)
    }

    override fun onBindViewHolder(holder: TradeProposalItemHolder, position: Int) {

        holder.items.text = tradeProposals[position].tp_items.split(",").joinToString { str ->
            str.filter { c -> c.isLetter() || c.isWhitespace() || c.isDigit() }
        }
        holder.itemCard.setBackgroundColor(Color.parseColor(cardColor))
        holder.itemCard.radius = 10f

        holder.itemView.setOnClickListener{
            clickListener.onItemClick(tradeProposals.get(position))
        }

        holder.junkshopName.text = tradeProposals[position].js_name
    }

    override fun getItemCount(): Int = tradeProposals.size

    inner class TradeProposalItemHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var itemCard: CardView = itemView.findViewById(R.id.f_r_trade_proposal_item_card)
        var items: TextView = itemView.findViewById(R.id.f_r_tp_items)
        var junkshopName: TextView = itemView.findViewById(R.id.f_r_tp_js_name)
    }

    interface ClickListener {
        fun onItemClick(tradeProposal: TradeProposalsRResultBody)
    }
}