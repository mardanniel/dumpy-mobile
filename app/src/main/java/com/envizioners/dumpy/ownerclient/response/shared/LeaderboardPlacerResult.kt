package com.envizioners.dumpy.ownerclient.response.shared

import com.google.gson.annotations.SerializedName

data class LeaderboardPlacerResult(
    @SerializedName("count") val count: Int,
    @SerializedName("exist") val exist: Boolean,
    @SerializedName("result") val result: List<LeaderboardPlacerResultBody>
)