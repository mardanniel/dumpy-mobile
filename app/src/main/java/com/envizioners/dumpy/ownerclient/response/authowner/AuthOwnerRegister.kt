package com.envizioners.dumpy.ownerclient.response.authowner

import com.google.gson.annotations.SerializedName

data class AuthOwnerRegister(
    @SerializedName("address_brgy") val address_brgy: String,
    @SerializedName("address_city") val address_city: String,
    @SerializedName("address_street") val address_street: String,
    @SerializedName("agree") val agree: String,
    @SerializedName("birthdate") val birthdate: String,
    @SerializedName("business_permit") val business_permit: String,
    @SerializedName("confirm-password") val confPassword: String,
    @SerializedName("contact") val contact: String,
    @SerializedName("email") val email: String,
    @SerializedName("fname") val fname: String,
    @SerializedName("gender") val gender: String,
    @SerializedName("js_address_barangay") val js_address_barangay: String,
    @SerializedName("js_address_city") val js_address_city: String,
    @SerializedName("js_address_street") val js_address_street: String,
    @SerializedName("js_name") val js_name: String,
    @SerializedName("junkshop_photo") val junkshop_photo: String,
    @SerializedName("lname") val lname: String,
    @SerializedName("mayor_residence") val mayor_residence: String,
    @SerializedName("mname") val mname: String,
    @SerializedName("password") val password: String,
    @SerializedName("sanitary_permit") val sanitary_permit: String
)