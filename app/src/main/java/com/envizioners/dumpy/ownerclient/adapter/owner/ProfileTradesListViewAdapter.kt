package com.envizioners.dumpy.ownerclient.adapter.owner

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.envizioners.dumpy.ownerclient.R

class ProfileTradesListViewAdapter(
    private val tradesList: List<List<String>>
): RecyclerView.Adapter<ProfileTradesListViewAdapter.PTLItemHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PTLItemHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_trades_list_item, parent, false)
        return PTLItemHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: PTLItemHolder, position: Int) {
        holder.itemName.text = "Item: ${tradesList[position][0]}"
        holder.itemType.text = "Item type: ${tradesList[position][1]}"
        holder.itemPPQ.text = "Item price per quantity: ₱${tradesList[position][2]} per ${tradesList[position][4]}"
        holder.itemPointsMultiplier.text = "Item points multiplier: ${tradesList[position][3]}"
    }

    override fun getItemCount(): Int = tradesList.size

    inner class PTLItemHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var itemName: TextView = itemView.findViewById(R.id.f_o_ptl_item_name)
        var itemType: TextView = itemView.findViewById(R.id.f_o_ptl_item_type)
        var itemPPQ: TextView = itemView.findViewById(R.id.f_o_ptl_item_ppq)
        var itemPointsMultiplier: TextView = itemView.findViewById(R.id.f_o_ptl_item_points_mutiplier)
    }
}