package com.envizioners.dumpy.ownerclient.response.upload

import com.google.gson.annotations.SerializedName

data class TradeItemImage(
    @SerializedName("error") val error: Int,
    @SerializedName("name") val name: String,
    @SerializedName("size") val size: Int,
    @SerializedName("tmp_name") val tmp_name: String,
    @SerializedName("type") val type: String
)