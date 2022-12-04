package com.envizioners.dumpy.ownerclient.adapter.recycler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.envizioners.dumpy.ownerclient.R
import com.envizioners.dumpy.ownerclient.response.points.CouponDetail

class CouponsViewAdapter(
    val couponsObtained: List<CouponDetail>
): RecyclerView.Adapter<CouponsViewAdapter.CouponsItemHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CouponsItemHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_coupon_item, parent, false)
        return CouponsItemHolder(view)
    }

    override fun onBindViewHolder(holder: CouponsItemHolder, position: Int) {
        holder.couponJunkshopName.text = couponsObtained[position].js_name
        holder.couponName.text = "Coupon Name: ${couponsObtained[position].coupon_name}"
        holder.couponPrice.text = "₱ ${couponsObtained[position].coupon_price}"
    }

    override fun getItemCount(): Int = couponsObtained.size

    inner class CouponsItemHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val couponName: TextView = itemView.findViewById(R.id.f_r_coupon_name)
        val couponJunkshopName: TextView = itemView.findViewById(R.id.f_r_coupon_js_name)
        val couponPrice: TextView = itemView.findViewById(R.id.f_r_coupon_price)
    }
}