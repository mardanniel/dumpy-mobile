package com.envizioners.dumpy.ownerclient.model_interfaces

import com.envizioners.dumpy.ownerclient.response.authrecycler.AuthRecyclerLogin
import com.envizioners.dumpy.ownerclient.response.authrecycler.AuthRecyclerRegister
import com.envizioners.dumpy.ownerclient.response.points.CouponsResult
import com.envizioners.dumpy.ownerclient.response.points.ExchangePointsResult
import com.envizioners.dumpy.ownerclient.response.points.SpecificCoupons
import com.envizioners.dumpy.ownerclient.response.recycler_main.EstablishmentLoc
import com.envizioners.dumpy.ownerclient.response.recycler_main.RecyclerProfile
import com.envizioners.dumpy.ownerclient.response.recycler_main.TradeProposalsR
import com.envizioners.dumpy.ownerclient.response.shared.ChatMenuRList
import com.envizioners.dumpy.ownerclient.response.shared.TradeProposalCountByStatus
import com.envizioners.dumpy.ownerclient.response.tradeproposal.TradeProposalsStatus
import com.envizioners.dumpy.ownerclient.response.tradeproposal.TradesLists
import com.envizioners.dumpy.ownerclient.response.validation.FormValidationResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface DumpyRecyclerService {

    @FormUrlEncoded
    @POST("api/auth/recycler/login")
    suspend fun authenticateRecycler(
        @Field("email") userEmail: String,
        @Field("password") userPassword: String
    ): Response<AuthRecyclerLogin>

    @FormUrlEncoded
    @POST("api/auth/recycler/register")
    suspend fun registerRecycler(
        @Field("email") email: String,
        @Field("fname") fname: String,
        @Field("mname") mname: String,
        @Field("lname") lname: String,
        @Field("contact") contact: String,
        @Field("birthdate") birthdate: String,
        @Field("address_street") street: String,
        @Field("address_brgy") barangay: String,
        @Field("address_city") city: String,
        @Field("password") password: String,
        @Field("confirm-password") confPass: String,
        @Field("agree") agree: String
    ): Response<AuthRecyclerRegister>

    @GET("api/recycler/profile/{userID}")
    suspend fun getRecyclerInfo(
        @Path("userID") userID: Int
    ): Response<RecyclerProfile>

    @Multipart
    @POST("api/recycler/insert-trade")
    suspend fun insertTrade(
        @Part tradeItemImages: @JvmSuppressWildcards List<MultipartBody.Part>,
        @Part("tradeitem") tradeItems: @JvmSuppressWildcards String,
        @Part("tradequantity") tradeItemQuantities: @JvmSuppressWildcards String,
        @Part("trademeasurement") tradeMeasurements: @JvmSuppressWildcards String,
        @Part("js_id") junkshopID: @JvmSuppressWildcards Int,
        @Part("email") userEmail: String,
        @Part("user_id") userID: Int
    ): Response<FormValidationResponse>

    @GET("api/staff/get-js-tradeslist/{junkshopId}")
    suspend fun getTradeList(
        @Path("junkshopId") junkshopID: Int
    ): Response<TradesLists>

    @GET("api/recycler/tp-statuses/{user_id}")
    suspend fun getTradeProposalsStatus(
        @Path("user_id") userID: Int
    ): Response<TradeProposalCountByStatus>

    @GET("api/RecyclerController/getNearest/{lat}/{lng}")
    suspend fun getNearestEstablishments(
        @Path("lat") lat: Double,
        @Path("lng") lng: Double,
    ): Response<EstablishmentLoc>

    @GET("api/recycler/get-conversations/{recyclerID}")
    suspend fun getConversationList(
        @Path("recyclerID") recyclerID: Int
    ): Response<ChatMenuRList>

    @FormUrlEncoded
    @POST("api/recycler/get-conversation-token")
    suspend fun getConversationToken(
        @Field("chat-sender") chat_sender: Int,
        @Field("chat-receiver") chat_receiver: Int
    ): Response<String>

    @FormUrlEncoded
    @POST("api/recycler/trade-proposals")
    suspend fun getTradeProposals(
        @Field("user_id") userID: Int,
        @Field("tp_status_code") tpStatusCode: Int
    ): Response<TradeProposalsR>

    @GET("api/recycler/exchange-points/{user_id}")
    suspend fun getExchangePointsList(
        @Path("user_id") userID: Int
    ): Response<ExchangePointsResult>

    @GET("api/recycler/get-coupon/{user_id}")
    suspend fun getCouponsList(
        @Path("user_id") userID: Int
    ): Response<CouponsResult>

    @FormUrlEncoded
    @POST("api/recycler/send-request-points")
    suspend fun sendRequest(
        @Field("user_id") userID: Int,
        @Field("js_id") establishmentID: Int
    ): Response<Int>

    @FormUrlEncoded
    @POST("api/recycler/get-specific-coupon")
    suspend fun getSpecificCoupons(
        @Field("user_id") userID: Int,
        @Field("js_id") establishmentID: Int
    ): Response<SpecificCoupons>

    @FormUrlEncoded
    @POST("api/recycler/cancel-trade")
    suspend fun cancelTrade(
        @Field("user_id") userID: Int,
        @Field("tp_id") tradeProposalID: Int
    ): Response<String>

    @FormUrlEncoded
    @POST("api/recycler/delete-trade")
    suspend fun deleteTrade(
        @Field("user_id") userID: Int,
        @Field("tp_id") tradeProposalID: Int,
        @Field("email") userEmail: String
    ): Response<String>

    @FormUrlEncoded
    @POST("api/recycler/proceed-trade")
    suspend fun proceedTrade(
        @Field("user_id") userID: Int,
        @Field("tp_id") tradeProposalID: Int,
        @Field("js_id") establishmentID: Int,
        @Field("transaction") transactionMode: String,
        @Field("coupon_name") couponName: String?
    ): Response<String>

    @FormUrlEncoded
    @POST("api/recycler/insert-rate")
    suspend fun insertRating(
        @Field("user_id") userID: Int,
        @Field("tp_id") tradeProposalID: Int,
        @Field("tp_rating") rating: Float
    ): Response<Int>

    @Multipart
    @POST("api/recycler/change-profile-picture")
    suspend fun changeRecyclerPic(
        @Part("user_id") userID: Int,
        @Part recyclerPic: MultipartBody.Part //userFile
    ): Response<Int>
}