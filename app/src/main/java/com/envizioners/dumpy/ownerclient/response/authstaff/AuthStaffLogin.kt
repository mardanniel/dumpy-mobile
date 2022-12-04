package com.envizioners.dumpy.ownerclient.response.authstaff

import com.envizioners.dumpy.ownerclient.response.shared.LoginFields
import com.google.gson.annotations.SerializedName

data class AuthStaffLogin(
    @SerializedName("exist") val exist: Boolean,
    @SerializedName("disabled") val disabled: Boolean,
    @SerializedName("result") val result: AuthStaffLoginResultCredentials?,
    @SerializedName("form_validation") val form_validation: LoginFields,
    @SerializedName("api_key") val api_key: String? = null
)