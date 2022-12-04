package com.envizioners.dumpy.ownerclient.response.authowner

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class AuthOwnerLoginResultCredentials(
    @SerializedName("js_owner_id") val jsOwnerId : String,
    @SerializedName("js_owner_fname") val jsOwnerFname : String,
    @SerializedName("js_owner_mname") val jsOwnerMname : String,
    @SerializedName("js_owner_lname") val jsOwnerLname : String,
    @SerializedName("js_owner_gender") val jsOwnerGender : String,
    @SerializedName("js_owner_email") val jsOwnerEmail : String,
    @SerializedName("js_owner_password") val jsOwnerPassword : String,
    @SerializedName("js_owner_contact") val jsOwnerContact : String,
    @SerializedName("js_owner_birthdate") val jsOwnerBirthdate : String,
    @SerializedName("js_owner_address") val jsOwnerAddress : String,
    @SerializedName("js_owner_verification_status") val jsOwnerVerificationStatus : String,
    @SerializedName("js_owner_verification_code") val jsOwnerVerificationCode : String,
    @SerializedName("js_account_creation_date_time") val jsAccountCreationDateTime : String,
    @SerializedName("js_owner_profile_image") val jsOwnerProfileImage: String
): Serializable