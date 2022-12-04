package com.envizioners.dumpy.ownerclient.response.authstaff

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class AuthStaffLoginResultCredentials(
    @SerializedName("dumpy_js_owner_id") val js_owner_id: String,
    @SerializedName("dumpy_js_staff_age") val js_staff_age: String,
    @SerializedName("dumpy_js_staff_email") val js_staff_email: String,
    @SerializedName("dumpy_js_staff_fname") val js_staff_fname: String,
    @SerializedName("dumpy_js_staff_hired_date") val js_staff_hired_date: String,
    @SerializedName("dumpy_js_staff_id") val js_staff_id: String,
    @SerializedName("dumpy_js_staff_lname") val js_staff_lname: String,
    @SerializedName("dumpy_js_staff_password") val js_staff_password: String,
    @SerializedName("dumpy_js_staff_username") val js_staff_username: String,
    @SerializedName("js_staff_contact") val js_staff_contact: String,
    @SerializedName("js_id") val js_id: String,
    @SerializedName("js_staff_profile_picture") val js_staff_profile_image: String
): Serializable