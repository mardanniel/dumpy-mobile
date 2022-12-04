package com.envizioners.dumpy.ownerclient.model_interfaces

import com.envizioners.dumpy.ownerclient.network.NotificationNetwork
import com.envizioners.dumpy.ownerclient.notification.PushNotification
import com.envizioners.dumpy.ownerclient.response.authowner.AuthOwnerLogin
import com.envizioners.dumpy.ownerclient.response.authowner.AuthOwnerRegister
import com.envizioners.dumpy.ownerclient.response.owner_main.AddStaffResponse
import com.envizioners.dumpy.ownerclient.response.owner_main.EstablishmentLocation
import com.envizioners.dumpy.ownerclient.response.owner_main.OwnerProfile
import com.envizioners.dumpy.ownerclient.response.points.ExchangeRequest
import com.envizioners.dumpy.ownerclient.response.shared.ChatMenuJList
import com.envizioners.dumpy.ownerclient.response.staff_main.StaffList
import com.envizioners.dumpy.ownerclient.response.tradeproposal.TradeProposal
import com.envizioners.dumpy.ownerclient.response.validation.FormValidationResponse
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import org.checkerframework.checker.nullness.compatqual.PolyNullDecl
import retrofit2.Response
import retrofit2.http.*

interface DumpyOwnerService {

    @Headers("Authorization: key=${NotificationNetwork.SERVER_KEY}", "Content-Type:${NotificationNetwork.CONTENT_TYPE}")
    @POST("fcm/send")
    suspend fun postNotification(
        @Body notification: PushNotification
    ): Response<ResponseBody>

    @FormUrlEncoded
    @POST("api/auth/owner/login")
    suspend fun authenticateOwner(
        @Field("email") userEmail: String,
        @Field("password") userPassword: String
    ): Response<AuthOwnerLogin>

    @Multipart
    @POST("api/auth/owner/register")
    suspend fun registerOwner(
        @Part("email") email: String,
        @Part("fname") fname: String,
        @Part("mname") mname: String,
        @Part("lname") lname: String,
        @Part("contact") contact: String,
        @Part("birthdate") birthdate: String,
        @Part("gender") gender: String,
        @Part("address_street") street: String,
        @Part("address_brgy") barangay: String,
        @Part("address_city") city: String,
        @Part("password") password: String,
        @Part("confirm-password") confPass: String,
        @Part("agree") agree: String,
        @Part("js_name") establishmentName: String,
        @Part("js_address_street") establishmentStreet: String,
        @Part("js_address_barangay") establishmentBarangay: String,
        @Part("js_address_city") establishmentCity: String,
        @Part establishmentPhoto: MultipartBody.Part,
        @Part businessPermit: MultipartBody.Part,
        @Part sanitaryPermit: MultipartBody.Part,
        @Part mayorResidence: MultipartBody.Part
    ): Response<AuthOwnerRegister>

    @GET("api/owner/trade-proposals/{ESTABLISHMENT_ID}")
    suspend fun getTradeProposals(
        @Path("ESTABLISHMENT_ID") junkshopID: Int
    ): Response<TradeProposal>

    @GET("api/owner/junkshop-staffs/{OWNER_ID}")
    suspend fun getStaffList(
        @Path("OWNER_ID") junkshopOwnerID: Int
    ): Response<StaffList>

    @GET("api/owner/get-conversations/{ESTABLISHMENT_ID}")
    suspend fun getConversationList(
        @Path("ESTABLISHMENT_ID") junkshopID: Int
    ): Response<ChatMenuJList>

    @GET("api/owner/js-location/{OWNER_ID}")
    suspend fun getEstablishmentLocation(
        @Path("OWNER_ID") junkshopOwnerID: Int
    ): Response<EstablishmentLocation>

    @FormUrlEncoded
    @POST("api/owner/save-location-junkshop")
    suspend fun saveEstablishmentLocation(
        @Field("js_id") establishmentID: Int,
        @Field("latitude") latitude: Double,
        @Field("longitude") longitude: Double
    ): Response<Int>

    @GET("api/owner/point-request-list/{js_id}")
    suspend fun getRequestList(
        @Path("js_id") establishmentID: Int
    ): Response<List<ExchangeRequest>>

    @FormUrlEncoded
    @POST("api/owner/process-point-request")
    suspend fun acceptExchangeRequest(
        @Field("req_id") exchangeReqID: Int
    ): Response<Int>

    @FormUrlEncoded
    @POST("api/owner/add-staff")
    suspend fun addStaff(
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("confirm-pass") confPassword: String,
        @Field("contact") contact: String,
        @Field("username") username: String,
        @Field("fname") fname: String,
        @Field("lname") lname: String,
        @Field("age") age: Int,
        @Field("owner_id") ownerID: Int
    ): Response<AddStaffResponse>

    @GET("api/owner/profile/{owner_id}")
    suspend fun getProfileDetails(
        @Path("owner_id") ownerId: Int
    ): Response<OwnerProfile>

    @FormUrlEncoded
    @POST("api/owner/change-status-staff")
    suspend fun toggleStatus(
        @Field("staff_id") staffID: Int,
        @Field("owner_id") ownerID: Int
    ): Response<Int>

    @FormUrlEncoded
    @POST("api/owner/delete-staff")
    suspend fun removeStaff(
        @Field("staff_id") staffID: Int,
        @Field("owner_id") ownerID: Int
    ): Response<Int>

    @FormUrlEncoded
    @POST("api/owner/save-trade-list-junkshop")
    suspend fun saveTradesList(
        @Field("js_id") establishmentID: Int,
        @Field("tradeitem[]") tradeItems: List<String>,
        @Field("tradeitemtype[]") tradeItemTypes: List<String>,
        @Field("tradeprice[]") tradePrices: List<String>,
        @Field("trademultiplier[]") tradePointsMultiplier: List<String>,
        @Field("tradequantity[]") tradeQuantityLabel: List<String>
    ): Response<Int>

    @Multipart
    @POST("api/owner/change-profile-picture")
    suspend fun changeOwnerPic(
        @Part("owner_id") ownerID: Int,
        @Part("email") ownerEmail: String,
        @Part ownerPic: MultipartBody.Part //userFile
    ): Response<Int>

    @GET("api/owner/check-tp-statuses/{establishment_id}")
    suspend fun checkTradeProposalStatus(
        @Path("establishment_id") establishmentID: Int
    ): Response<Int>

    @FormUrlEncoded
    @POST("https://dumpyph.com/api/owner/download-file")
    suspend fun getReport(
        @Field("owner_id") ownerID: Int,
        @Field("js_id") establishmentID: Int
    ): Response<ResponseBody>
}