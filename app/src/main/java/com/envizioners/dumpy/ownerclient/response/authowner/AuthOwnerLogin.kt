package com.envizioners.dumpy.ownerclient.response.authowner

import com.envizioners.dumpy.ownerclient.response.shared.LoginFields
import com.google.gson.annotations.SerializedName

data class AuthOwnerLogin(
    @SerializedName("exist") val exist : Boolean,
    @SerializedName("validated") val validated : Boolean,
    @SerializedName("form_validation") val form_validation: LoginFields,
    @SerializedName("result") val result : AuthOwnerLoginResult?,
    @SerializedName("api_key") val api_key: String? = null
)