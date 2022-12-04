package com.envizioners.dumpy.ownerclient.response.points

data class CouponsResult(
    val coupon_details: List<CouponDetail>,
    val message: String,
    val status: Boolean
)