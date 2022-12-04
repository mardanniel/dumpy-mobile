package com.envizioners.dumpy.ownerclient.adapter.shared

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.RecyclerView
import com.envizioners.dumpy.ownerclient.R
import com.envizioners.dumpy.ownerclient.response.shared.ChatMessagesItem
import java.text.SimpleDateFormat
import java.util.*

class ChatViewAdapter(
    val senderType: String,
    val receiverName: String,
    val chatLog : MutableList<ChatMessagesItem>
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_ONE = 1
    private val VIEW_TYPE_TWO = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_ONE){
            LeftChatMessageItemHolder(LayoutInflater.from(parent.context).inflate(R.layout.adapter_left_chat_message_item, parent, false))
        }else RightChatMessageItemHolder(LayoutInflater.from(parent.context).inflate(R.layout.adapter_right_chat_message_item, parent, false))
    }

    @SuppressLint("LongLogTag")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (chatLog[position].chat_sender_type == senderType){
            with(holder as RightChatMessageItemHolder){
                chatParticipantName.text = "You"
                chatMessageBody.text = chatLog[position].messageBody
                chatTimestamp.text = convertLongToTime(chatLog[position].createdAt)
            }
        }else{
            with(holder as LeftChatMessageItemHolder){
                chatParticipantName.text = receiverName
                chatMessageBody.text = chatLog[position].messageBody
                chatTimestamp.text = convertLongToTime(chatLog[position].createdAt)
            }
        }
    }

    override fun getItemCount(): Int = chatLog.size

    @SuppressLint("SimpleDateFormat")
    private fun convertLongToTime(time: Long): String {
        val date = Date(time)
        val format = SimpleDateFormat("MMM dd, yyyy HH:mm a")
        return format.format(date)
    }

    override fun getItemViewType(position: Int): Int {
        return if (chatLog[position].chat_sender_type == senderType){
            VIEW_TYPE_TWO
        }else VIEW_TYPE_ONE
    }

    inner class LeftChatMessageItemHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var chatConstraintLayout: ConstraintLayout = itemView.findViewById(R.id.f_left_chat_message_constraint_layout)
        var chatParticipantName: TextView = itemView.findViewById(R.id.f_left_chat_message_name)
        var chatCard: CardView = itemView.findViewById(R.id.f_left_chat_card)
        var chatMessageBody: TextView = itemView.findViewById(R.id.f_left_chat_message)
        var chatTimestamp: TextView = itemView.findViewById(R.id.f_left_chat_timestamp)
    }

    inner class RightChatMessageItemHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var chatConstraintLayout: ConstraintLayout = itemView.findViewById(R.id.f_right_chat_message_constraint_layout)
        var chatParticipantName: TextView = itemView.findViewById(R.id.f_right_chat_message_name)
        var chatCard: CardView = itemView.findViewById(R.id.f_right_chat_card)
        var chatMessageBody: TextView = itemView.findViewById(R.id.f_right_chat_message)
        var chatTimestamp: TextView = itemView.findViewById(R.id.f_right_chat_timestamp)
    }
}