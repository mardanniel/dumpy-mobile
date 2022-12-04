package com.envizioners.dumpy.ownerclient.adapter.owner

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.envizioners.dumpy.ownerclient.R

class ManageTradesListViewAdapter(
    private val tradesList: MutableList<List<String>>,
    private val onManageTradeItem: OnManageTradeItem,
): RecyclerView.Adapter<ManageTradesListViewAdapter.ManageTradeListItemHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ManageTradeListItemHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_trades_list_item, parent, false)
        return ManageTradeListItemHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ManageTradeListItemHolder, position: Int) {
        holder.itemName.text = "Item: ${tradesList[position][0]}"
        holder.itemType.text = "Item type: ${tradesList[position][1]}"
        holder.itemPPQ.text = "Item price per quantity: ₱${tradesList[position][2]} per ${tradesList[position][4]}"
        holder.itemPointsMultiplier.text = "Item points multiplier: ${tradesList[position][3]}"
        holder.itemAction.visibility = View.VISIBLE
        holder.itemEdit.setOnClickListener {
            onManageTradeItem.onEditClicked(position)
        }
    }

    override fun getItemCount(): Int = tradesList.size

    interface OnManageTradeItem {
        fun onEditClicked(position: Int)
    }

    inner class ManageTradeListItemHolder: RecyclerView.ViewHolder{
        var itemName: TextView
        var itemType: TextView
        var itemPPQ: TextView
        var itemPointsMultiplier: TextView
        var itemAction: ConstraintLayout
        var itemEdit: Button
        var itemRemove: Button

        constructor(itemView: View): super(itemView){
            itemName = itemView.findViewById(R.id.f_o_ptl_item_name)
            itemType = itemView.findViewById(R.id.f_o_ptl_item_type)
            itemPPQ = itemView.findViewById(R.id.f_o_ptl_item_ppq)
            itemPointsMultiplier = itemView.findViewById(R.id.f_o_ptl_item_points_mutiplier)
            itemAction = itemView.findViewById(R.id.f_o_ptl_action_items)
            itemEdit = itemView.findViewById(R.id.f_o_ptl_edit_item)
            itemRemove = itemView.findViewById(R.id.f_o_ptl_remove_item)

            itemRemove.setOnClickListener {
                tradesList.removeAt(absoluteAdapterPosition)
                notifyItemRemoved(absoluteAdapterPosition)
            }
        }
    }
}