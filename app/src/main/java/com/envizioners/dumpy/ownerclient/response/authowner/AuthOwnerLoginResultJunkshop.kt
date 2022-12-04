package com.envizioners.dumpy.ownerclient.response.authowner

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class AuthOwnerLoginResultJunkshop(
    @SerializedName("js_address") val js_address: String,
    @SerializedName("js_business_permit") val js_business_permit: String,
    @SerializedName("js_id") val js_id: String,
    @SerializedName("js_latitude") val js_latitude: String,
    @SerializedName("js_location_photo") val js_location_photo: String,
    @SerializedName("js_longitude") val js_longtitude: String,
    @SerializedName("js_mayor_residence") val js_mayor_residence: String,
    @SerializedName("js_name") val js_name: String,
    @SerializedName("js_owner_id") val js_owner_id: String,
    @SerializedName("js_rating") val js_rating: String,
    @SerializedName("js_sanitary_permit") val js_sanitary_permit: String,
    @SerializedName("js_trades_list") val js_trades_list: String
): Serializable