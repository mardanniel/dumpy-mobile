package com.envizioners.dumpy.ownerclient.response.recycler_main

import com.google.gson.annotations.SerializedName

data class RecyclerProfile(
    @SerializedName("user_address") val user_address: String,
    @SerializedName("user_age") val user_age: String,
    @SerializedName("user_contact") val user_contact: String,
    @SerializedName("user_email") val user_email: String,
    @SerializedName("user_full_name") val user_full_name: String,
    @SerializedName("user_profile_image") val user_profile_image: String,
    @SerializedName("user_rank") val user_rank: String
)