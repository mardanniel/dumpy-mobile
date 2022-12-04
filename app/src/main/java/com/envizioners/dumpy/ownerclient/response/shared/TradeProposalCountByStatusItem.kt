package com.envizioners.dumpy.ownerclient.response.shared

import com.google.gson.annotations.SerializedName

data class TradeProposalCountByStatusItem(
    @SerializedName("count") val count: String,
    @SerializedName("tp_status") val tp_status: String
)