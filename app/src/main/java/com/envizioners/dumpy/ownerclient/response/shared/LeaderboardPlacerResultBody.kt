package com.envizioners.dumpy.ownerclient.response.shared

import com.google.gson.annotations.SerializedName

data class LeaderboardPlacerResultBody(
    @SerializedName("user_accumulated_pts") val user_accumulated_pts: String,
    @SerializedName("user_fname") val user_fname: String,
    @SerializedName("user_lname") val user_lname: String,
    @SerializedName("user_mname") val user_mname: String
)