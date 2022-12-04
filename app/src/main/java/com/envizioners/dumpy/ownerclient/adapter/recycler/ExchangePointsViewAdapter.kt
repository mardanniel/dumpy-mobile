package com.envizioners.dumpy.ownerclient.adapter.recycler

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.envizioners.dumpy.ownerclient.R
import com.envizioners.dumpy.ownerclient.response.points.PointsDetail
import kotlin.math.floor

private var previousExpandedPosition = -1
private var mExpandedPosition = -1

class ExchangePointsViewAdapter(
    val pointsFromDifferentJunkshops: List<PointsDetail>,
    val onButtonClicked: OnButtonClicked
): RecyclerView.Adapter<ExchangePointsViewAdapter.ExchangePointsItemHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExchangePointsItemHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_exchange_points_item, parent, false)
        return ExchangePointsItemHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ExchangePointsItemHolder, position: Int) {
        var isExpanded: Boolean = position == mExpandedPosition
        holder.totalAccumulatedPoints.text = "${floor(pointsFromDifferentJunkshops[position].user_points.toDouble())} pts"
        holder.junkshopSource.text = pointsFromDifferentJunkshops[position].js_name
        holder.exchangeRequestButton.text = "Exchange points to ₱${floor(pointsFromDifferentJunkshops[position].user_points.toDouble() / 500).toInt()}"
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
            onButtonClicked.onButtonClicked(position)
        }
    }

    override fun getItemCount(): Int = pointsFromDifferentJunkshops.size

    interface OnButtonClicked {
        fun onButtonClicked(position: Int)
    }

    inner class ExchangePointsItemHolder : RecyclerView.ViewHolder {
        var expandableConstraintLayout: ConstraintLayout
        var totalAccumulatedPoints: TextView
        var junkshopSource: TextView
        var exchangeRequestButton: Button

        constructor(itemView: View) : super(itemView) {
            expandableConstraintLayout = itemView.findViewById(R.id.f_r_expandable_section)
            totalAccumulatedPoints = itemView.findViewById(R.id.f_r_exchange_points_total_acc_pts)
            junkshopSource = itemView.findViewById(R.id.f_r_exchange_points_js_name)
            exchangeRequestButton = itemView.findViewById(R.id.exchange_points_submit)
        }
    }
}