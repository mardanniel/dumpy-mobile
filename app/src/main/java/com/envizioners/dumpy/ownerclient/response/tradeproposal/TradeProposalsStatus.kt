package com.envizioners.dumpy.ownerclient.response.tradeproposal

import com.google.gson.annotations.SerializedName

data class TradeProposalsStatus(
    @SerializedName("exist") val exist : Boolean,
    @SerializedName("count") val count : Int,
    @SerializedName("result") val result : List<String>?,
)
