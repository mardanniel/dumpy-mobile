package com.envizioners.dumpy.ownerclient.response.authowner

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class AuthOwnerLoginResult(
    @SerializedName("junkshop") val junkshop: AuthOwnerLoginResultJunkshop,
    @SerializedName("owner") val owner: AuthOwnerLoginResultCredentials
): Serializable