package com.envizioners.dumpy.ownerclient.adapter.shared

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.envizioners.dumpy.ownerclient.R
import com.envizioners.dumpy.ownerclient.response.shared.LeaderboardPlacer
import com.envizioners.dumpy.ownerclient.response.shared.LeaderboardPlacerResultBody

class LeaderboardViewAdapter(
    private val topRecyclerList: List<LeaderboardPlacerResultBody>
): RecyclerView.Adapter<LeaderboardViewAdapter.LeaderboardPlacerHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaderboardPlacerHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_leaderboard_item, parent, false)
        return LeaderboardPlacerHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: LeaderboardPlacerHolder, position: Int) {
        holder.leaderboardPlacerName.text = "${topRecyclerList[position].user_fname} ${topRecyclerList[position].user_mname} ${topRecyclerList[position].user_lname}"
        holder.leaderboardPlacerAccPts.text = "${topRecyclerList[position].user_accumulated_pts} points"
        holder.leaderboardPlacerPlace.text = "Top ${position + 1}"
    }

    override fun getItemCount(): Int = topRecyclerList.size

    inner class LeaderboardPlacerHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var leaderboardPlacerPlace: TextView = itemView.findViewById(R.id.leaderboard_placer_place)
        var leaderboardPlacerName: TextView = itemView.findViewById(R.id.leaderboard_placer_name)
        var leaderboardPlacerAccPts: TextView = itemView.findViewById(R.id.leaderboard_placer_acc_points)
    }
}