package com.envizioners.dumpy.ownerclient.response.points

data class ExchangePointsResult(
    val current_pts: String?,
    val message: String,
    val points_details: List<PointsDetail>,
    val status: Boolean
)