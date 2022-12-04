package com.envizioners.dumpy.ownerclient.response.validation

import com.google.gson.annotations.SerializedName

data class FormValidationResponse(
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: String?,
    @SerializedName("status") val status: Boolean
)