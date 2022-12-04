package com.envizioners.dumpy.ownerclient.adapter.shared

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.envizioners.dumpy.ownerclient.R
import com.envizioners.dumpy.ownerclient.fragments.shared.ChatFragment
import com.envizioners.dumpy.ownerclient.response.shared.ChatMenuRListResultBody

class ChatMenuRViewAdapter(
    val userID: Int,
    val userType: String,
    val chatMenuList : List<ChatMenuRListResultBody>,
    val fragmentManager: FragmentManager,
): RecyclerView.Adapter<ChatMenuRViewAdapter.ChatMenuRItemHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatMenuRItemHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_chat_menu_item, parent, false)
        return ChatMenuRItemHolder(view)
    }

    override fun onBindViewHolder(holder: ChatMenuRItemHolder, position: Int) {
        holder.chatMenuName.text = chatMenuList[position].js_name
        holder.itemView.setOnClickListener {
            val dataBundle = Bundle()
            val chatFragment = ChatFragment()
            dataBundle.putBoolean("searchJunkshopReferrer", false)
            dataBundle.putString("senderID", userID.toString())
            dataBundle.putString("senderType", userType)
            dataBundle.putString("receiverID", chatMenuList[position].chat_receiver)
            dataBundle.putString("receiverName", chatMenuList[position].js_name)
            dataBundle.putString("chatToken", chatMenuList[position].chat_token)
            chatFragment.arguments = dataBundle
            fragmentManager.beginTransaction()
                .addToBackStack(null)
                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right)
                .replace(R.id.fragment_container, chatFragment).commit()
        }
    }

    override fun getItemCount(): Int = chatMenuList.size

    inner class ChatMenuRItemHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var chatMenuName: TextView = itemView.findViewById(R.id.f_chat_name)
    }
}