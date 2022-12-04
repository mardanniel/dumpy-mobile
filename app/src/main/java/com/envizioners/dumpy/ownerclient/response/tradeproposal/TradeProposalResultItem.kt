package com.envizioners.dumpy.ownerclient.response.tradeproposal

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class TradeProposalResultItem(
    @SerializedName("js_id") val js_id: String,
    @SerializedName("tp_date_time") val tp_date_time: String,
    @SerializedName("tp_id") val tp_id: String,
    @SerializedName("tp_image_names") val tp_image_names: String,
    @SerializedName("tp_items") val tp_items: String,
    @SerializedName("tp_labels") val tp_labels: String,
    @SerializedName("tp_quantities") val tp_quantities: String,
    @SerializedName("tp_status") val tp_status: String,
    @SerializedName("tp_types") val tp_types: String,
    @SerializedName("user_email") val user_email: String,
    @SerializedName("user_fname") val user_fname: String,
    @SerializedName("user_id") val user_id: String,
    @SerializedName("user_lname") val user_lname: String,
    @SerializedName("user_mname") val user_mname: String,
    @SerializedName("tp_transaction") val tp_transaction: String?,
    @SerializedName("coupon_name") val coupon_name: String?,
    @SerializedName("coupon_value") val coupon_val: String?,
    @SerializedName("user_address") val user_address: String?
): Serializable