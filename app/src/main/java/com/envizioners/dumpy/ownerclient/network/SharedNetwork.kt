package com.envizioners.dumpy.ownerclient.network

import android.content.Context
import com.envizioners.dumpy.ownerclient.model_implementations.SharedServiceSource
import com.envizioners.dumpy.ownerclient.model_implementations.StaffServiceSource
import com.envizioners.dumpy.ownerclient.model_interfaces.DumpySharedService
import com.envizioners.dumpy.ownerclient.model_interfaces.DumpyStaffService

class SharedNetwork(
    context: Context
) {
    private val dumpySharedService: DumpySharedService by lazy {
        DumpyMainNetwork().buildAPI(DumpySharedService::class.java, context)
    }
    val sharedClient = SharedServiceSource(dumpySharedService)
}