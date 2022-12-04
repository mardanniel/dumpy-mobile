package com.envizioners.dumpy.ownerclient.adapter.owner

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.envizioners.dumpy.ownerclient.R
import com.envizioners.dumpy.ownerclient.response.points.ExchangeRequest

private var previousExpandedPosition = -1
private var mExpandedPosition = -1

class ExchangeRequestViewAdapter(
    val requestsFromDifferentRecyclers: List<ExchangeRequest>,
    val onButtonClicked: OnButtonClicked
): RecyclerView.Adapter<ExchangeRequestViewAdapter.ExchangeRequestItemHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExchangeRequestItemHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_exchange_request_item, parent, false)
        return ExchangeRequestItemHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ExchangeRequestItemHolder, position: Int) {
        var isExpanded: Boolean = position == mExpandedPosition
        holder.totalAccumulatedPoints.text = "${requestsFromDifferentRecyclers[position].request_points} pts"
        holder.recyclerName.text = "${requestsFromDifferentRecyclers[position].user_fname} ${requestsFromDifferentRecyclers[position].user_lname}"
        holder.exchangeRequestButton.text = "Accept Exchange Request (₱${requestsFromDifferentRecyclers[position].request_points.toInt() / 500})"
        holder.requestTimestamp.text = requestsFromDifferentRecyclers[position].request_timestamp
        holder.expandableConstraintLayout.visibility = if(isExpanded) View.VISIBLE else View.GONE
        holder.itemView.isActivated = isExpanded
        if (isExpanded){
            previousExpandedPosition = position
        }
        holder.itemView.setOnClickListener {
            mExpandedPosition = if(isExpanded) -1 else position
            notifyItemChanged(previousExpandedPosition)
            notifyItemChanged(position)
        }

        holder.exchangeRequestButton.setOnClickListener {
            onButtonClicked.onButtonClicked(position, requestsFromDifferentRecyclers[position].request_id.toInt())
        }
    }

    override fun getItemCount(): Int = requestsFromDifferentRecyclers.size

    inner class ExchangeRequestItemHolder : RecyclerView.ViewHolder {
        var expandableConstraintLayout: ConstraintLayout
        var totalAccumulatedPoints: TextView
        var recyclerName: TextView
        var requestTimestamp: TextView
        var exchangeRequestButton: Button

        constructor(itemView: View) : super(itemView) {
            expandableConstraintLayout = itemView.findViewById(R.id.f_o_expandable_section)
            totalAccumulatedPoints = itemView.findViewById(R.id.f_o_exchange_request_total_acc_pts)
            recyclerName = itemView.findViewById(R.id.f_o_exchange_requester_full_name)
            requestTimestamp = itemView.findViewById(R.id.f_o_exchange_request_timestamp)
            exchangeRequestButton = itemView.findViewById(R.id.exchange_request_submit)
        }
    }

    interface OnButtonClicked {
        fun onButtonClicked(pos: Int,requestID: Int)
    }
}