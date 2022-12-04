package com.envizioners.dumpy.ownerclient.response.tradeproposal

import android.net.Uri
import java.io.Serializable

data class CreateTradeItem(
    val itemName: String,
    val itemImage: Uri,
    val itemQuantity: String,
    val itemMeasurement: String
): Serializable
