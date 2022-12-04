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
import com.envizioners.dumpy.ownerclient.response.shared.ChatMenuJListResultBody
import com.envizioners.dumpy.ownerclient.response.shared.ChatMenuRListResultBody

class ChatMenuJViewAdapter(
    val userID: Int,
    val userType: String,
    val chatMenuList : List<ChatMenuJListResultBody>,
    val fragmentManager: FragmentManager,
): RecyclerView.Adapter<ChatMenuJViewAdapter.ChatMenuJItemHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatMenuJItemHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_chat_menu_item, parent, false)
        return ChatMenuJItemHolder(view)
    }

    override fun onBindViewHolder(holder: ChatMenuJItemHolder, position: Int) {
        var userFullName = "${chatMenuList[position].user_fname} ${chatMenuList[position].user_mname} ${chatMenuList[position].user_lname}"
        holder.chatMenuName.text = userFullName
        holder.itemView.setOnClickListener {
            val dataBundle = Bundle()
            val chatFragment = ChatFragment()
            dataBundle.putString("senderID", userID.toString())
            dataBundle.putString("senderType", userType)
            dataBundle.putString("receiverID", chatMenuList[position].chat_sender)
            dataBundle.putString("receiverName", userFullName)
            dataBundle.putString("chatToken", chatMenuList[position].chat_token)
            chatFragment.arguments = dataBundle
            fragmentManager.beginTransaction()
                .addToBackStack(null)
                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right)
                .replace(R.id.fragment_container, chatFragment).commit()
        }
    }

    override fun getItemCount(): Int = chatMenuList.size

    inner class ChatMenuJItemHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var chatMenuName: TextView = itemView.findViewById(R.id.f_chat_name)
    }
}