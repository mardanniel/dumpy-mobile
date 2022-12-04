package com.envizioners.dumpy.ownerclient.model_implementations

import com.envizioners.dumpy.ownerclient.model_interfaces.DumpySharedService
import com.envizioners.dumpy.ownerclient.response.shared.LeaderboardPlacerResult
import com.envizioners.dumpy.ownerclient.response.shared.StaffInfo
import com.envizioners.dumpy.ownerclient.response.shared.TradeProposalCountByStatus
import com.envizioners.dumpy.ownerclient.response.tradeproposal.TradeProposalResult
import com.envizioners.dumpy.ownerclient.response.validation.FormValidationResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Part

class SharedServiceSource(
    private val dumpyService: DumpySharedService
) {
    suspend fun getLeaderboard(): Response<LeaderboardPlacerResult> {
        return dumpyService.getLeaderboard()
    }

    suspend fun insertReport(
        source_id: Int,
        source_role: String,
        report_text: String,
        report_type: String,
        report_image: MultipartBody.Part? // report_img
    ): Response<FormValidationResponse> {
        return dumpyService.insertReport(
            source_id,
            source_role,
            report_text,
            report_type,
            report_image
        )
    }

    suspend fun setUDT(
        userID: Int,
        token: String,
        userRole: String
    ): Response<Boolean> {
        return dumpyService.setUDT(
            userID,
            token,
            userRole
        )
    }

    suspend fun unsetUDT(
        userID: Int,
        userRole: String
    ): Response<Boolean> {
        return dumpyService.unsetUDT(
            userID,
            userRole
        )
    }

    suspend fun getStaffInfo(
        staffID: Int,
        ownerID: Int
    ): Response<StaffInfo> {
        return dumpyService.getStaffInfo(
            staffID,
            ownerID
        )
    }

    suspend fun getPendingProposals(
        establishmentID: Int
    ): Response<TradeProposalResult> {
        return dumpyService.getPendingProposals(
            establishmentID
        )
    }

    suspend fun getProceededProposals(
        establishmentID: Int
    ): Response<TradeProposalResult> {
        return dumpyService.getProceededProposals(
            establishmentID
        )
    }

    suspend fun getTradeProposalStatuses(
        establishmentID: Int
    ): Response<TradeProposalCountByStatus> {
        return dumpyService.getTradeProposalStatuses(
            establishmentID
        )
    }
}