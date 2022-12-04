package com.envizioners.dumpy.ownerclient.model_interfaces


import com.envizioners.dumpy.ownerclient.response.shared.LeaderboardPlacerResult
import com.envizioners.dumpy.ownerclient.response.shared.StaffInfo
import com.envizioners.dumpy.ownerclient.response.shared.TradeProposalCountByStatus
import com.envizioners.dumpy.ownerclient.response.tradeproposal.TradeProposalResult
import com.envizioners.dumpy.ownerclient.response.validation.FormValidationResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*


interface DumpySharedService {

    @GET("api/shared/leaderboard")
    suspend fun getLeaderboard(): Response<LeaderboardPlacerResult>

    @Multipart
    @POST("api/shared/report")
    suspend fun insertReport(
        @Part("report_resource_id") source_id: Int,
        @Part("report_source_role") source_role: String,
        @Part("report_text") report_text: String,
        @Part("reportType") report_type: String,
        @Part report_image: MultipartBody.Part?
    ): Response<FormValidationResponse>

    @FormUrlEncoded
    @POST("api/shared/set-udt")
    suspend fun setUDT(
        @Field("user_id") userID: Int,
        @Field("user_device_token") token: String,
        @Field("user_source_role") userRole: String
    ): Response<Boolean>

    @FormUrlEncoded
    @POST("api/shared/unset-udt")
    suspend fun unsetUDT(
        @Field("user_id") userID: Int,
        @Field("user_source_role") userRole: String
    ): Response<Boolean>

    @FormUrlEncoded
    @POST("api/shared/staff-profile")
    suspend fun getStaffInfo(
        @Field("staff_id") staffID: Int,
        @Field("owner_id") ownerID: Int
    ): Response<StaffInfo>

    @GET("api/shared/pending-proposal/{js_id}")
    suspend fun getPendingProposals(
        @Path("js_id") establishmentID: Int
    ): Response<TradeProposalResult>

    @GET("api/staff/proceeded-proposal/{js_id}")
    suspend fun getProceededProposals(
        @Path("js_id") establishmentID: Int
    ): Response<TradeProposalResult>

    @GET("api/shared/get-trade-proposal/{js_id}")
    suspend fun getTradeProposalStatuses(
        @Path("js_id") establishmentID: Int
    ): Response<TradeProposalCountByStatus>
}