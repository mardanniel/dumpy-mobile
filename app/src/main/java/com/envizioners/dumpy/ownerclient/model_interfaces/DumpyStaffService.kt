package com.envizioners.dumpy.ownerclient.model_interfaces

import com.envizioners.dumpy.ownerclient.response.authstaff.AuthStaffLogin
import com.envizioners.dumpy.ownerclient.response.shared.ChatMenuJList
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface DumpyStaffService {

    @FormUrlEncoded
    @POST("api/auth/staff/login")
    suspend fun authenticateStaff(
        @Field("email") userEmail: String,
        @Field("password") userPassword: String
    ): Response<AuthStaffLogin>

    @FormUrlEncoded
    @POST("api/staff/accept-proposal")
    suspend fun decideTrade(
        @Field("tp_id") tradeProposalID: Int,
        @Field("message") tpMessage: String,
        @Field("staff_id") staffID: Int,
        @Field("decision") decision: Int
    ): Response<String>

    @FormUrlEncoded
    @POST("api/staff/conclude-trade-proposal")
    suspend fun concludeTrade(
        @Field("tp_id")  tradeProposalID: Int,
        @Field("tradequantity[]") tradeQuantities: List<Int>
    ): Response<String>

    @Multipart
    @POST("api/staff/change-profile-picture")
    suspend fun changeStaffPic(
        @Part("staff_id") staffID: Int,
        @Part("email") staffEmail: String,
        @Part staffPic: MultipartBody.Part // userFile
    ): Response<Int>
}