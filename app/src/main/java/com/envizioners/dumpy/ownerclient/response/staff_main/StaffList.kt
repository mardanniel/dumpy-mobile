package com.envizioners.dumpy.ownerclient.response.staff_main

import com.google.gson.annotations.SerializedName

data class StaffList(
    @SerializedName("count") val count: Int,
    @SerializedName("exist") val exist: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: List<StaffResultBody>
)