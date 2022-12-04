package com.envizioners.dumpy.ownerclient.response.authrecycler

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class AuthRecyclerLoginResultCredentials(
    @SerializedName("user_accumulated_pts") val user_accumulated_pts: String,
    @SerializedName("user_address") val user_address: String,
    @SerializedName("user_birthdate") val user_birthdate: String,
    @SerializedName("user_contact") val user_contact: String,
    @SerializedName("user_device_token") val user_device_token: String,
    @SerializedName("user_email") val user_email: String,
    @SerializedName("user_fname") val user_fname: String,
    @SerializedName("user_id") val user_id: String,
    @SerializedName("user_lname") val user_lname: String,
    @SerializedName("user_mname") val user_mname: String,
    @SerializedName("user_profile_image") val user_profile_image: String,
): Serializable