package com.envizioners.dumpy.ownerclient.network

import android.content.Context
import com.envizioners.dumpy.ownerclient.model_implementations.StaffServiceSource
import com.envizioners.dumpy.ownerclient.model_interfaces.DumpyStaffService

class StaffNetwork(
    context: Context
) {
    private val dumpyStaffService: DumpyStaffService by lazy {
        DumpyMainNetwork().buildAPI(DumpyStaffService::class.java, context)
    }
    val staffClient = StaffServiceSource(dumpyStaffService)
}