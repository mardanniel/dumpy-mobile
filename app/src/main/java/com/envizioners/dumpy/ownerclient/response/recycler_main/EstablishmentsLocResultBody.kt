package com.envizioners.dumpy.ownerclient.response.recycler_main

import com.google.gson.annotations.SerializedName

data class EstablishmentsLocResultBody(
    @SerializedName("distance") val distance: String,
    @SerializedName("js_address") val js_address: String,
    @SerializedName("js_id") val js_id: String,
    @SerializedName("js_latitude") val js_latitude: String,
    @SerializedName("js_longtitude") val js_longtitude: String,
    @SerializedName("js_name") val js_name: String,
    @SerializedName("js_owner_fname") val js_owner_fname: String,
    @SerializedName("js_owner_lname") val js_owner_lname: String,
    @SerializedName("js_rating") val js_rating: String,
    @SerializedName("js_trades_list") val js_trades_list: List<List<String>>
)