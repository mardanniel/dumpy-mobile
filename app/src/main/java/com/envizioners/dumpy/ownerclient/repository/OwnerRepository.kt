package com.envizioners.dumpy.ownerclient.repository

import android.content.Context
import android.util.Log
import com.envizioners.dumpy.ownerclient.network.OwnerNetwork
import com.envizioners.dumpy.ownerclient.network.NotificationNetwork
import com.envizioners.dumpy.ownerclient.notification.PushNotification
import com.envizioners.dumpy.ownerclient.response.authowner.AuthOwnerLogin
import com.envizioners.dumpy.ownerclient.response.authowner.AuthOwnerRegister
import com.envizioners.dumpy.ownerclient.response.owner_main.AddStaffResponse
import com.envizioners.dumpy.ownerclient.response.owner_main.EstablishmentLocation
import com.envizioners.dumpy.ownerclient.response.owner_main.OwnerProfile
import com.envizioners.dumpy.ownerclient.response.points.ExchangeRequest
import com.envizioners.dumpy.ownerclient.response.staff_main.StaffList
import com.envizioners.dumpy.ownerclient.response.tradeproposal.TradeProposal
import com.envizioners.dumpy.ownerclient.response.validation.FormValidationResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response

class OwnerRepository(
    context: Context
) {

    private val passedContext = context

    suspend fun sendNotification(notification: PushNotification, activity: String) {
        try {
            val response = NotificationNetwork.notifAPI.postNotification(notification)
            if(response.isSuccessful){
                Log.i(activity, "Response Success: ${response.body()}")
            } else{
                Log.i(activity, "Response Error: ${response.errorBody()}")
            }
        }catch (e: Exception){
            Log.i(activity, "Exception: ${e.toString()}")
        }
    }

    suspend fun registerOwner(
        email: String,
        fname: String,
        mname: String,
        lname: String,
        contact: String,
        birthdate: String,
        gender: String,
        street: String,
        barangay: String,
        city: String,
        password: String,
        confPass: String,
        agree: String,
        establishmentName: String,
        establishmentStreet: String,
        establishmentBarangay: String,
        establishmentCity: String,
        establishmentPhoto: MultipartBody.Part,
        businessPermit: MultipartBody.Part,
        sanitaryPermit: MultipartBody.Part,
        mayorResidence: MultipartBody.Part
    ): AuthOwnerRegister {
        val request = OwnerNetwork(passedContext).ownerClient.registerOwner(
            email,
            fname,
            mname,
            lname,
            contact,
            birthdate,
            gender,
            street,
            barangay,
            city,
            password,
            confPass,
            agree,
            establishmentName,
            establishmentStreet,
            establishmentBarangay,
            establishmentCity,
            establishmentPhoto,
            businessPermit,
            sanitaryPermit,
            mayorResidence
        )
        return if(!request.isSuccessful){
            var gson = Gson()
            val type = object : TypeToken<AuthOwnerRegister>() {}.type
            var errorResponse: AuthOwnerRegister = gson.fromJson(request.errorBody()!!.charStream(), type)
            errorResponse
        }else {
            request.body()!!
        }
    }

    suspend fun authenticateOwner(
        userEmail: String,
        userPassword: String
    ): AuthOwnerLogin? {
        val request = OwnerNetwork(passedContext).ownerClient.authenticateOwner(
            userEmail,
            userPassword
        )
        return if(!request.isSuccessful){
            var gson = Gson()
            val type = object : TypeToken<AuthOwnerLogin>() {}.type
            var errorResponse: AuthOwnerLogin = gson.fromJson(request.errorBody()!!.charStream(), type)
            errorResponse
        }else {
            request.body()!!
        }
    }

    suspend fun getTradeProposals(junkshopID: Int): TradeProposal? {
        val request = OwnerNetwork(passedContext).ownerClient.getTradeProposals(junkshopID)
        return if(!request.isSuccessful){
            var gson = Gson()
            val type = object : TypeToken<TradeProposal>() {}.type
            var errorResponse: TradeProposal = gson.fromJson(request.errorBody()!!.charStream(), type)
            errorResponse
        }else {
            request.body()!!
        }
    }

    suspend fun getStaffList(junkshopOwnerID: Int): StaffList? {
        val request = OwnerNetwork(passedContext).ownerClient.getStaffList(junkshopOwnerID)
        return if(!request.isSuccessful){
            var gson = Gson()
            val type = object : TypeToken<StaffList>() {}.type
            var errorResponse: StaffList = gson.fromJson(request.errorBody()!!.charStream(), type)
            errorResponse
        }else {
            request.body()!!
        }
    }

    suspend fun getEstablishmentLocation(junkshopOwnerID: Int): EstablishmentLocation? {
        val request = OwnerNetwork(passedContext).ownerClient.getEstablishmentLocation(junkshopOwnerID)
        return if(!request.isSuccessful){
            var gson = Gson()
            val type = object : TypeToken<EstablishmentLocation>() {}.type
            var errorResponse: EstablishmentLocation = gson.fromJson(request.errorBody()!!.charStream(), type)
            errorResponse
        }else {
            request.body()!!
        }
    }

    suspend fun saveEstablishmentLocation(
        establishmentID: Int,
        latitude: Double,
        longitude: Double
    ): Int {
        val request = OwnerNetwork(passedContext).ownerClient.saveEstablishmentLocation(
            establishmentID,
            latitude,
            longitude
        )
        return if(!request.isSuccessful){
            var gson = Gson()
            val type = object : TypeToken<Int>() {}.type
            var errorResponse: Int = gson.fromJson(request.errorBody()!!.charStream(), type)
            errorResponse
        }else {
            request.body()!!
        }
    }


    suspend fun addStaff(
        email: String,
        password: String,
        confPassword: String,
        contact: String,
        username: String,
        fname: String,
        lname: String,
        age: Int,
        ownerID: Int
    ): AddStaffResponse {
        val request = OwnerNetwork(passedContext).ownerClient.addStaff(
            email,
            password,
            confPassword,
            contact,
            username,
            fname,
            lname,
            age,
            ownerID
        )
        return if(!request.isSuccessful){
            var gson = Gson()
            val type = object : TypeToken<AddStaffResponse>() {}.type
            var errorResponse: AddStaffResponse = gson.fromJson(request.errorBody()!!.charStream(), type)
            errorResponse
        }else {
            request.body()!!
        }
    }

    suspend fun getRequestList(
        establishmentID: Int
    ): List<ExchangeRequest> {
        val request = OwnerNetwork(passedContext).ownerClient.getRequestList(
            establishmentID
        )
        return if(!request.isSuccessful){
            var gson = Gson()
            val type = object : TypeToken<List<ExchangeRequest>>() {}.type
            var errorResponse: List<ExchangeRequest> = gson.fromJson(request.errorBody()!!.charStream(), type)
            errorResponse
        }else {
            request.body()!!
        }
    }

    suspend fun acceptExchangeRequest(
        exchangeRequestID: Int
    ): Int {
        val request = OwnerNetwork(passedContext).ownerClient.acceptExchangeRequest(
            exchangeRequestID
        )
        return if(!request.isSuccessful){
            var gson = Gson()
            val type = object : TypeToken<Int>() {}.type
            var errorResponse: Int = gson.fromJson(request.errorBody()!!.charStream(), type)
            errorResponse
        }else {
            request.body()!!
        }
    }

    suspend fun getProfileDetails(
        ownerId: Int
    ): OwnerProfile {
        val request = OwnerNetwork(passedContext).ownerClient.getProfileDetails(
            ownerId
        )
        return if(!request.isSuccessful){
            var gson = Gson()
            val type = object : TypeToken<OwnerProfile>() {}.type
            var errorResponse: OwnerProfile = gson.fromJson(request.errorBody()!!.charStream(), type)
            errorResponse
        }else {
            request.body()!!
        }
    }

    suspend fun toggleStatus(
        staffID: Int,
        ownerID: Int
    ): Int {
        val request = OwnerNetwork(passedContext).ownerClient.toggleStatus(
            staffID, ownerID
        )
        return if(!request.isSuccessful){
            var gson = Gson()
            val type = object : TypeToken<Int>() {}.type
            var errorResponse: Int = gson.fromJson(request.errorBody()!!.charStream(), type)
            errorResponse
        }else {
            request.body()!!
        }
    }

    suspend fun removeStaff(
        staffID: Int,
        ownerID: Int
    ): Int {
        val request = OwnerNetwork(passedContext).ownerClient.removeStaff(
            staffID, ownerID
        )
        return if(!request.isSuccessful){
            var gson = Gson()
            val type = object : TypeToken<Int>() {}.type
            var errorResponse: Int = gson.fromJson(request.errorBody()!!.charStream(), type)
            errorResponse
        }else {
            request.body()!!
        }
    }

    suspend fun saveTradesList(
        establishmentID: Int,
        tradeItems: List<String>,
        tradeItemTypes: List<String>,
        tradePrices: List<String>,
        tradePointsMultiplier: List<String>,
        tradeQuantityLabel: List<String>
    ): Int {
        var request = OwnerNetwork(passedContext).ownerClient.saveTradesList(
            establishmentID,
            tradeItems,
            tradeItemTypes,
            tradePrices,
            tradePointsMultiplier,
            tradeQuantityLabel
        )
        return if(!request.isSuccessful){
            var gson = Gson()
            val type = object : TypeToken<Int>() {}.type
            var errorResponse: Int = gson.fromJson(request.errorBody()!!.charStream(), type)
            errorResponse
        }else {
            request.body()!!
        }
    }

    suspend fun checkTradeProposalStatus(
        establishmentID: Int
    ): Int {
        val request = OwnerNetwork(passedContext).ownerClient.checkTradeProposalStatus(
            establishmentID
        )
        return if(!request.isSuccessful){
            var gson = Gson()
            val type = object : TypeToken<Int>() {}.type
            var errorResponse: Int = gson.fromJson(request.errorBody()!!.charStream(), type)
            errorResponse
        }else {
            request.body()!!
        }
    }

    suspend fun getReport(
        ownerID: Int,
        establishmentID: Int
    ): Any {
        val request = OwnerNetwork(passedContext).ownerClient.getReport(
            ownerID, establishmentID
        )
        return if(!request.isSuccessful){
            var gson = Gson()
            val type = object : TypeToken<Int>() {}.type
            var errorResponse: Int = gson.fromJson(request.errorBody()!!.charStream(), type)
            errorResponse
        }else {
            request.body()!!
        }
    }
}
