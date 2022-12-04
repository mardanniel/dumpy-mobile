package com.envizioners.dumpy.ownerclient.response.shared

import com.google.gson.annotations.SerializedName

data class StaffInfo(
    @SerializedName("dumpy_js_staff_email") val js_staff_email: String,
    @SerializedName("dumpy_js_staff_fname") val js_staff_fname: String,
    @SerializedName("dumpy_js_staff_hired_date") val js_staff_hired_date: String,
    @SerializedName("dumpy_js_staff_id") val js_staff_id: String,
    @SerializedName("dumpy_js_staff_lname") val js_staff_lname: String,
    @SerializedName("dumpy_js_staff_username") val js_staff_uname: String,
    @SerializedName("dumpy_js_staff_age") val js_staff_age: String,
    @SerializedName("js_staff_contact") val js_staff_contact: String,
    @SerializedName("js_name") val js_name: String,
    @SerializedName("js_owner_fname") val js_owner_fname: String,
    @SerializedName("js_owner_lname") val js_owner_lname: String,
    @SerializedName("js_owner_mname") val js_owner_mname: String,
    @SerializedName("js_staff_profile_picture") val js_staff_profile_picture: String
)