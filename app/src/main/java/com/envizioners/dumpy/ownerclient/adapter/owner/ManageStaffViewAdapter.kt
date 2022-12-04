package com.envizioners.dumpy.ownerclient.adapter.owner

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.envizioners.dumpy.ownerclient.R
import com.envizioners.dumpy.ownerclient.response.staff_main.StaffResultBody

class ManageStaffViewAdapter (
    private val staffList: List<StaffResultBody>,
    private val clickListener: ClickListener
): RecyclerView.Adapter<ManageStaffViewAdapter.StaffItemHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StaffItemHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_manage_staff_item, parent, false)
        return StaffItemHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: StaffItemHolder, position: Int) {
        holder.staffItemCard.setCardBackgroundColor(Color.parseColor(if (staffList[position].account_status == "0") "#E53935" else "#8BC34A"))
        holder.fullname.text = "${staffList[position].dumpy_js_staff_fname} ${staffList[position].dumpy_js_staff_lname}"
        holder.dateHired.text = "Hired Date: ${staffList[position].dumpy_js_staff_hired_date}"
        holder.itemView.setOnClickListener{
            clickListener.onItemClick(position ,staffList[position])
        }
    }

    override fun getItemCount(): Int = staffList.size

    inner class StaffItemHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var staffItemCard: CardView = itemView.findViewById(R.id.staff_item_card)
        var fullname: TextView = itemView.findViewById(R.id.staff_item_full_name)
        var dateHired: TextView = itemView.findViewById(R.id.staff_item_hired_date)
    }

    interface ClickListener {
        fun onItemClick(position: Int,staffResult: StaffResultBody)
    }
}