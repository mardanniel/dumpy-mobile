package com.envizioners.dumpy.ownerclient.adapter.recycler

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.envizioners.dumpy.ownerclient.R
import com.envizioners.dumpy.ownerclient.fragments.biv.ImageViewer
import com.envizioners.dumpy.ownerclient.fragments.shared.ChatFragment
import com.envizioners.dumpy.ownerclient.repository.RecyclerRepository
import com.github.piasy.biv.BigImageViewer
import com.github.piasy.biv.loader.glide.GlideImageLoader
import com.github.piasy.biv.view.BigImageView

class TradeProposalDetailsViewAdapter(
    private val tradeItems: List<String>,
    private val tradeQuantities: List<String>,
    private val tradeLabels: List<String>,
    private val tradeTypes: List<String>,
    private val tradeImages: List<String>,
    private val userEmail: String,
    private val context: Context,
    private val sfm: FragmentManager
): RecyclerView.Adapter<TradeProposalDetailsViewAdapter.TradeProposalItemHolder>() {

    private val baseUrl = "https://dumpyph.com/uploads/trade-proposals/"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TradeProposalItemHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_trade_proposal_details_item, parent, false)
        return TradeProposalItemHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: TradeProposalItemHolder, position: Int) {
        holder.itemName.text = "${tradeItems[position]} (${tradeTypes[position]})"
        holder.itemQuantity.text = "${tradeQuantities[position]} ${tradeLabels[position]}"
        var folderPath = baseUrl + userEmail + "/" + tradeImages[position]
        Log.i("ImagePath: ", folderPath)
        val circularProgressDrawable = CircularProgressDrawable(context)
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 30f
        circularProgressDrawable.start()
        Glide.with(holder.itemView)
            .load(folderPath)
            .placeholder(circularProgressDrawable)
            .centerCrop()
            .into(holder.itemImage)

        holder.itemImage.setOnClickListener {
            val dataBundle = Bundle()
            val imgView = ImageViewer()
            dataBundle.putString("uriString", folderPath)
            imgView.arguments = dataBundle
            sfm.beginTransaction()
                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right)
                .replace(R.id.fragment_container, imgView).addToBackStack(null).commit()
        }
    }

    override fun getItemCount(): Int = tradeItems.size

    inner class TradeProposalItemHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val itemName: TextView = itemView.findViewById(R.id.tpd_item_name)
        val itemQuantity: TextView = itemView.findViewById(R.id.tpd_item_quantity)
        val itemImage: ImageView = itemView.findViewById(R.id.tpd_image)
    }
}