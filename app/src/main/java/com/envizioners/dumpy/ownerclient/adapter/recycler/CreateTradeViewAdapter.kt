package com.envizioners.dumpy.ownerclient.adapter.recycler

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.envizioners.dumpy.ownerclient.R
import com.envizioners.dumpy.ownerclient.response.tradeproposal.CreateTradeItem

class CreateTradeViewAdapter(
    private val createTradeItems: MutableList<CreateTradeItem>
): RecyclerView.Adapter<CreateTradeViewAdapter.CreateTradeItemHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CreateTradeItemHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_create_trade_item, parent, false)
        return CreateTradeItemHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CreateTradeItemHolder, position: Int) {
        holder.itemName.text = createTradeItems[position].itemName
        holder.itemImage.setImageURI(createTradeItems[position].itemImage)
        holder.itemQuantity.text = "${createTradeItems[position].itemQuantity} ${createTradeItems[position].itemMeasurement}"
    }

    override fun getItemCount(): Int = createTradeItems.size

    inner class CreateTradeItemHolder: RecyclerView.ViewHolder {
        var removeButton: Button
        var itemImage: ImageView
        var itemName: TextView
        var itemQuantity: TextView

        constructor(itemView: View) : super(itemView){
            removeButton = itemView.findViewById(R.id.f_r_create_trade_remove_item)
            itemImage = itemView.findViewById(R.id.f_r_create_trade_item_image)
            itemName = itemView.findViewById(R.id.f_r_create_trade_items_name)
            itemQuantity = itemView.findViewById(R.id.f_r_create_trade_quantity)

            removeButton.setOnClickListener {
                createTradeItems.removeAt(absoluteAdapterPosition)
                notifyItemRemoved(absoluteAdapterPosition)
            }
        }
    }
}