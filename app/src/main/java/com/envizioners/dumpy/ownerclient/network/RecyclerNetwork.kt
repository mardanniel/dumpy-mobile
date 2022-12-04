package com.envizioners.dumpy.ownerclient.network

import android.content.Context
import com.envizioners.dumpy.ownerclient.model_implementations.RecyclerServiceSource
import com.envizioners.dumpy.ownerclient.model_interfaces.DumpyRecyclerService

class RecyclerNetwork(
    context: Context
) {

    private val dumpyRecyclerService: DumpyRecyclerService by lazy {
        DumpyMainNetwork().buildAPI(DumpyRecyclerService::class.java, context)
    }

    val recyclerClient = RecyclerServiceSource(dumpyRecyclerService)
}

