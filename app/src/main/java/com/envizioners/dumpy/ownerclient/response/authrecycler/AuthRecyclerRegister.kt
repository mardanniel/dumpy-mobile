package com.envizioners.dumpy.ownerclient.response.authrecycler

import com.google.gson.annotations.SerializedName

data class AuthRecyclerRegister(
    @SerializedName("address_brgy") val address_brgy: String,
    @SerializedName("address_city") val address_city: String,
    @SerializedName("address_street") val address_street: String,
    @SerializedName("agree") val agree: String,
    @SerializedName("birthdate") val birthdate: String,
    @SerializedName("confirm-password") val confirm_password: String,
    @SerializedName("contact") val contact: String,
    @SerializedName("email") val email: String,
    @SerializedName("fname") val fname: String,
    @SerializedName("lname") val lname: String,
    @SerializedName("mname") val mname: String,
    @SerializedName("password") val password: String
)