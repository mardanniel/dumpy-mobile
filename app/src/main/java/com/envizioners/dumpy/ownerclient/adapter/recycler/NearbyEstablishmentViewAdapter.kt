package com.envizioners.dumpy.ownerclient.adapter.recycler

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.envizioners.dumpy.ownerclient.R
import com.envizioners.dumpy.ownerclient.fragments.recycler.CalculatorFragment
import com.envizioners.dumpy.ownerclient.fragments.recycler.CreateTradeFragment
import com.envizioners.dumpy.ownerclient.fragments.shared.ChatFragment
import com.envizioners.dumpy.ownerclient.fragments.shared.ChatMenuFragment
import com.envizioners.dumpy.ownerclient.response.recycler_main.EstablishmentsLocResultBody
import com.google.gson.Gson

private var previousExpandedPosition = -1
private var mExpandedPosition = -1

class NearbyEstablishmentViewAdapter(
    private val recyclerID: Int,
    private val nearbyEstablishmentList: List<EstablishmentsLocResultBody>,
    private val clickListener: ClickListener,
    val fragmentManager: FragmentManager
): RecyclerView.Adapter<NearbyEstablishmentViewAdapter.NearbyEstablishmentItemHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NearbyEstablishmentItemHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_nearby_establishment_item, parent, false)
        return NearbyEstablishmentItemHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: NearbyEstablishmentItemHolder, position: Int) {
        var isExpanded: Boolean = position == mExpandedPosition
        holder.nelName.text = nearbyEstablishmentList[position].js_name
        holder.nelAddress.text = "Establishment Address:\n${nearbyEstablishmentList[position].js_address}"
        holder.nelDistance.text = "Distance from your location:\n${String.format("%.2f", nearbyEstablishmentList[position].distance.toDouble())} meters"
        holder.nelIsNearby.text = if (nearbyEstablishmentList[position].distance.toDouble() < 150.0){
            "< 150m"
        }else{
            "> 150m"
        }
        holder.nelTradesList.text = constructTradesList(nearbyEstablishmentList[position].js_trades_list)
        holder.expandableNELSection.visibility = if(isExpanded) View.VISIBLE else View.GONE
        holder.itemView.isActivated = isExpanded
        if (isExpanded){
            previousExpandedPosition = position
        }
        holder.itemView.setOnClickListener {
            clickListener.onItemClick(
                position,
                nearbyEstablishmentList[position].js_latitude.toDouble(),
                nearbyEstablishmentList[position].js_longtitude.toDouble()
            )
            mExpandedPosition = if(isExpanded) -1 else position
            notifyItemChanged(previousExpandedPosition)
            notifyItemChanged(position)
        }
    }

    override fun getItemCount(): Int = nearbyEstablishmentList.size

    inner class NearbyEstablishmentItemHolder : RecyclerView.ViewHolder {
        var nelName: TextView
        var nelAddress: TextView
        var nelDistance: TextView
        var nelIsNearby: TextView
        var expandableNELSection: ConstraintLayout
        var nelTradesList: TextView
        var nelContact: Button
        var nelCalculator: Button
        var nelTrade: Button

        constructor(itemView: View): super(itemView){
            nelName = itemView.findViewById(R.id.f_r_nel_name)
            nelAddress = itemView.findViewById(R.id.f_r_nel_establishment_address)
            nelDistance = itemView.findViewById(R.id.f_r_nel_distance)
            nelIsNearby = itemView.findViewById(R.id.f_r_nel_is_nearby)
            expandableNELSection = itemView.findViewById(R.id.f_r_nel_section)
            nelTradesList = itemView.findViewById(R.id.f_r_nel_trades_list)
            nelContact = itemView.findViewById(R.id.f_r_nel_contact)
            nelCalculator = itemView.findViewById(R.id.f_r_nel_calculator)
            nelTrade = itemView.findViewById(R.id.f_r_nel_trade)

            nelTrade.setOnClickListener {
                val gson = Gson()
                val args = Bundle()
                val tradesList = gson.toJson(nearbyEstablishmentList[absoluteAdapterPosition].js_trades_list)
                val ctf = CreateTradeFragment()
                args.putString("tradesList", tradesList)
                args.putString("junkshopID", nearbyEstablishmentList[absoluteAdapterPosition].js_id)
                ctf.arguments = args
                fragmentManager.beginTransaction()
                    .addToBackStack(null)
                    .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right)
                    .replace(R.id.fragment_container, ctf).commit()
            }

            nelCalculator.setOnClickListener {
                val gson = Gson()
                val dataBundle = Bundle()
                val calcf = CalculatorFragment()
                val tradesList = gson.toJson(nearbyEstablishmentList[absoluteAdapterPosition].js_trades_list)
                dataBundle.putString("tradesList", tradesList)
                dataBundle.putString("junkshopID", nearbyEstablishmentList[absoluteAdapterPosition].js_id)
                calcf.arguments = dataBundle
                fragmentManager.beginTransaction()
                    .addToBackStack(null)
                    .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right)
                    .replace(R.id.fragment_container, calcf).commit()
            }

            nelContact.setOnClickListener {
                val dataBundle = Bundle()
                val chatFragment = ChatFragment()
                dataBundle.putBoolean("searchJunkshopReferrer", true)
                dataBundle.putString("senderID", recyclerID.toString())
                dataBundle.putString("senderType", "DUMPY_RECYCLER")
                dataBundle.putString("receiverID", nearbyEstablishmentList[absoluteAdapterPosition].js_id)
                dataBundle.putString("receiverName", nearbyEstablishmentList[absoluteAdapterPosition].js_name)
                chatFragment.arguments = dataBundle
                fragmentManager.beginTransaction()
                    .addToBackStack(null)
                    .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right)
                    .replace(R.id.fragment_container, chatFragment).commit()
            }
        }
    }

    private fun constructTradesList(tradesList: List<List<String>>): String{
        var finalString = ""

        tradesList.forEach {
            finalString += "• ${it[0]} (${it[1]}): ₱${it[2]} per ${it[4]}\n"
        }
        return finalString
    }

    interface ClickListener {
        fun onItemClick(position: Int,lat: Double, lng: Double)
    }
}