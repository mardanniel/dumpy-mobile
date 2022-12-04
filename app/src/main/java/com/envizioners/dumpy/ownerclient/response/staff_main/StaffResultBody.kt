package com.envizioners.dumpy.ownerclient.response.staff_main

import com.google.gson.annotations.SerializedName

data class StaffResultBody(
    @SerializedName("dumpy_js_staff_fname") val dumpy_js_staff_fname: String,
    @SerializedName("dumpy_js_staff_hired_date") val dumpy_js_staff_hired_date: String,
    @SerializedName("dumpy_js_staff_id") val dumpy_js_staff_id: String,
    @SerializedName("dumpy_js_staff_lname") val dumpy_js_staff_lname: String,
    @SerializedName("account_status") var account_status: String
)