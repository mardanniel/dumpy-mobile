package com.envizioners.dumpy.ownerclient.response.owner_main

import com.google.gson.annotations.SerializedName

data class OwnerProfile(
    @SerializedName("js_business_permit") val js_business_permit: String,
    @SerializedName("js_location_photo") val js_location_photo: String,
    @SerializedName("js_mayor_residence") val js_mayor_residence: String,
    @SerializedName("js_name") val js_name: String,
    @SerializedName("js_owner_address") val js_owner_address: String,
    @SerializedName("js_owner_age") val js_owner_age: Int,
    @SerializedName("js_owner_profile_image") val js_owner_profile_image: String,
    @SerializedName("js_owner_contact") val js_owner_contact: String,
    @SerializedName("js_owner_email") val js_owner_email: String,
    @SerializedName("js_owner_name") val js_owner_name: String,
    @SerializedName("js_rating") val js_rating: String,
    @SerializedName("js_sanitary_permit") val js_sanitary_permit: String,
    @SerializedName("js_trades_list") val js_trades_list: List<List<String>>
)