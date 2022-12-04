package com.envizioners.dumpy.ownerclient.response.shared

import com.google.gson.annotations.SerializedName

data class LoginFields(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("check_pass") val checkPass: String,
)