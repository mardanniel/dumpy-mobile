package com.envizioners.dumpy.ownerclient.response.recycler_main

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class TradeProposalsRResultBody(
    @SerializedName("js_id") val js_id: String,
    @SerializedName("js_name") val js_name: String,
    @SerializedName("tp_accepted_date_time") val tp_accepted_date_time: String?,
    @SerializedName("tp_cancel_date_time") val tp_cancel_date_time: String?,
    @SerializedName("tp_creation_date_time") val tp_creation_date_time: String?,
    @SerializedName("tp_finish_date_time") val tp_finish_date_time: String?,
    @SerializedName("tp_id") val tp_id: String,
    @SerializedName("tp_image_names") val tp_image_names: String,
    @SerializedName("tp_items") val tp_items: String,
    @SerializedName("tp_items_prices") val tp_items_prices: String,
    @SerializedName("tp_items_total_price") val tp_items_total_price: String,
    @SerializedName("tp_labels") val tp_labels: String,
    @SerializedName("tp_message") val tp_message: String?,
    @SerializedName("tp_payment") val tp_payment: String?,
    @SerializedName("tp_proceed_date_time") val tp_proceed_date_time: String?,
    @SerializedName("tp_quantities") val tp_quantities: String,
    @SerializedName("tp_rating") val tp_rating: String?,
    @SerializedName("tp_status") val tp_status: String,
    @SerializedName("tp_transaction") val tp_transaction: String?,
    @SerializedName("tp_types") val tp_types: String,
    @SerializedName("user_id") val user_id: String
): Serializable