package com.envizioners.dumpy.ownerclient.response.authrecycler

import com.envizioners.dumpy.ownerclient.response.shared.LoginFields
import com.google.gson.annotations.SerializedName

data class AuthRecyclerLogin(
    @SerializedName("exist") val exist: Boolean,
    @SerializedName("validated") val validated: Boolean,
    @SerializedName("result") val result: AuthRecyclerLoginResultCredentials?,
    @SerializedName("form_validation") val form_validation: LoginFields,
    @SerializedName("api_key") val api_key: String? = null
)