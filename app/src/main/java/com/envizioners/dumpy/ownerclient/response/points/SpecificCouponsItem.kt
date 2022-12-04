package com.envizioners.dumpy.ownerclient.response.points

import com.google.gson.annotations.SerializedName

data class SpecificCouponsItem(
    @SerializedName("coupon_name") val coupon_name: String,
    @SerializedName("coupon_price") val coupon_price: String
)