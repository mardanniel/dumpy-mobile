package com.envizioners.dumpy.ownerclient.response.shared

import com.google.gson.annotations.SerializedName
import java.util.Date

data class ChatMessagesItem(
    @SerializedName("chat_receiver") val chat_receiver: Int,
    @SerializedName("chat_receiver_type") val chat_receiver_type: String,
    @SerializedName("chat_sender") val chat_sender: Int,
    @SerializedName("chat_sender_type") val chat_sender_type: String,
    @SerializedName("createdAt") val createdAt: Long,
    @SerializedName("messageBody") val messageBody: String?
)