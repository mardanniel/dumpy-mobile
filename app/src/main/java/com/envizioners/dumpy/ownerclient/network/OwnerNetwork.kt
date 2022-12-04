package com.envizioners.dumpy.ownerclient.network

import android.content.Context
import com.envizioners.dumpy.ownerclient.model_implementations.OwnerServiceSource
import com.envizioners.dumpy.ownerclient.model_interfaces.DumpyOwnerService

class OwnerNetwork(
    context: Context
) {

    private val dumpyOwnerService: DumpyOwnerService by lazy {
        DumpyMainNetwork().buildAPI(DumpyOwnerService::class.java, context)
    }

    val ownerClient = OwnerServiceSource(dumpyOwnerService)
}