package com.envizioners.dumpy.ownerclient.response.recycler_main

import com.google.gson.annotations.SerializedName

data class EstablishmentLoc(
    @SerializedName("result") val result: List<EstablishmentsLocResultBody>,
    @SerializedName("status") val status: Boolean
)