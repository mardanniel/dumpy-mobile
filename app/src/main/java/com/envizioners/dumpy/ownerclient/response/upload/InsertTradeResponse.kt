package com.envizioners.dumpy.ownerclient.response.upload

import com.google.gson.annotations.SerializedName

data class InsertTradeResponse(
    @SerializedName("result") val result: InsertTradeResultBody,
    @SerializedName("status") val status: Boolean
)