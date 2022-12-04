package com.envizioners.dumpy.ownerclient.response.owner_main

import com.google.gson.annotations.SerializedName

data class EstablishmentLocation(
    @SerializedName("js_latitude") val js_latitude: String?,
    @SerializedName("js_longtitude") val js_longtitude: String?,
    @SerializedName("js_name") val js_name: String?
)