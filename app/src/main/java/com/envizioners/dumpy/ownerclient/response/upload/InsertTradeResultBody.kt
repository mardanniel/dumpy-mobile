package com.envizioners.dumpy.ownerclient.response.upload

import com.google.gson.annotations.SerializedName

data class InsertTradeResultBody(
    @SerializedName("image") val image: TradeItemImage
)