package com.envizioners.dumpy.ownerclient.response.owner_main

import com.google.gson.annotations.SerializedName

data class AddStaffResponse(
    @SerializedName("age") val age: String,
    @SerializedName("confirm-pass") val confirm_pass: String,
    @SerializedName("contact") val contact: String,
    @SerializedName("email") val email: String,
    @SerializedName("fname") val fname: String,
    @SerializedName("lname") val lname: String,
    @SerializedName("owner_id") val owner_id: String,
    @SerializedName("password") val password: String,
    @SerializedName("username") val username: String
)