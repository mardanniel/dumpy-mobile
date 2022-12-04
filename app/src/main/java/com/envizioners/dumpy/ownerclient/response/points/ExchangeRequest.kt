package com.envizioners.dumpy.ownerclient.response.points

import com.google.gson.annotations.SerializedName

data class ExchangeRequest(
    @SerializedName("exchange_request_id") val request_id: String,
    @SerializedName("exchange_request_points") val request_points: String,
    @SerializedName("user_fname") val user_fname: String,
    @SerializedName("user_lname") val user_lname: String,
    @SerializedName("er_timestamp") val request_timestamp: String
)