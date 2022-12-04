package com.envizioners.dumpy.ownerclient.model_implementations

import com.envizioners.dumpy.ownerclient.model_interfaces.DumpyStaffService
import com.envizioners.dumpy.ownerclient.response.authstaff.AuthStaffLogin
import com.envizioners.dumpy.ownerclient.response.shared.ChatMenuJList
import okhttp3.MultipartBody
import retrofit2.Response

class StaffServiceSource (
    private val dumpyService: DumpyStaffService
){
    suspend fun authenticateStaff(
        userEmail: String,
        userPassword: String
    ): Response<AuthStaffLogin> {
        return dumpyService.authenticateStaff(userEmail, userPassword)
    }

    suspend fun decideTrade(
        tradeProposalID: Int,
        tpMessage: String,
        staffID: Int,
        decision: Int
    ): Response<String> {
        return dumpyService.decideTrade(
            tradeProposalID,
            tpMessage,
            staffID,
            decision
        )
    }

    suspend fun concludeTrade(
        tradeProposalID: Int,
        tradeQuantities: List<Int>
    ): Response<String> {
        return dumpyService.concludeTrade(
            tradeProposalID,
            tradeQuantities
        )
    }

    suspend fun changeStaffPic(
        staffID: Int,
        staffEmail: String,
        staffPic: MultipartBody.Part //userFile
    ): Response<Int> {
        return dumpyService.changeStaffPic(
            staffID, staffEmail, staffPic
        )
    }
}