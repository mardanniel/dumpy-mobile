package com.envizioners.dumpy.ownerclient.response.shared

data class ChatMenuJListResultBody(
    val chat_sender: String,
    val chat_token: String,
    val user_fname: String,
    val user_lname: String,
    val user_mname: String
)