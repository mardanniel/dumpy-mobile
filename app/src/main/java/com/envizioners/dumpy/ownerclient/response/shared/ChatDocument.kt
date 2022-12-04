package com.envizioners.dumpy.ownerclient.response.shared

import com.google.firebase.firestore.ServerTimestamp
import java.util.*

data class ChatDocument(
    val count: Int,
    val messages: List<ChatMessagesItem>,
    @ServerTimestamp val createdAt: Date? = null,
)
