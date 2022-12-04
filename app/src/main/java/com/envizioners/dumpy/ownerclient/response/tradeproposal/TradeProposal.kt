package com.envizioners.dumpy.ownerclient.response.tradeproposal

import com.google.gson.annotations.SerializedName

data class TradeProposal(
    @SerializedName("exist") val exist: Boolean,
    @SerializedName("message") val message: Int,
    @SerializedName("result") val tradeProposalResult: TradeProposalResult
)