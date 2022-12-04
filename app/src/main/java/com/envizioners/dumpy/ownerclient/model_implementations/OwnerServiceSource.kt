package com.envizioners.dumpy.ownerclient.model_implementations

import com.envizioners.dumpy.ownerclient.model_interfaces.DumpyOwnerService
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
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.Part
import retrofit2.http.Path

class OwnerServiceSource (
    private val dumpyService: DumpyOwnerService
){
    suspend fun authenticateOwner(
        userEmail: String,
        userPassword: String
    ): Response<AuthOwnerLogin> {
        return dumpyService.authenticateOwner(userEmail, userPassword)
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
    ): Response<AuthOwnerRegister> {
        return dumpyService.registerOwner(
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
    }

    suspend fun getTradeProposals(junkshopID: Int): Response<TradeProposal> {
        return dumpyService.getTradeProposals(junkshopID)
    }

    suspend fun getStaffList(junkshopOwnerID: Int): Response<StaffList>{
        return dumpyService.getStaffList(junkshopOwnerID)
    }

    suspend fun getConversationList(junkshopID: Int): Response<ChatMenuJList> {
        return dumpyService.getConversationList(junkshopID)
    }

    suspend fun getEstablishmentLocation(junkshopOwnerID: Int): Response<EstablishmentLocation> {
        return dumpyService.getEstablishmentLocation(junkshopOwnerID)
    }

    suspend fun saveEstablishmentLocation(
        establishmentID: Int,
        latitude: Double,
        longitude: Double
    ): Response<Int> {
        return dumpyService.saveEstablishmentLocation(
            establishmentID,
            latitude,
            longitude
        )
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
    ): Response<AddStaffResponse> {
        return dumpyService.addStaff(
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
    }

    suspend fun getRequestList(
        establishmentID: Int
    ): Response<List<ExchangeRequest>> {
        return dumpyService.getRequestList(
            establishmentID
        )
    }

    suspend fun acceptExchangeRequest(
        exchangeRequestID: Int
    ): Response<Int> {
        return dumpyService.acceptExchangeRequest(
            exchangeRequestID
        )
    }

    suspend fun getProfileDetails(
        ownerId: Int
    ): Response<OwnerProfile> {
        return dumpyService.getProfileDetails(
            ownerId
        )
    }

    suspend fun toggleStatus(
        staffID: Int,
        ownerID: Int
    ): Response<Int> {
        return dumpyService.toggleStatus(
            staffID,
            ownerID
        )
    }

    suspend fun removeStaff(
        staffID: Int,
        ownerID: Int
    ): Response<Int> {
        return dumpyService.removeStaff(
            staffID,
            ownerID
        )
    }

    suspend fun saveTradesList(
        establishmentID: Int,
        tradeItems: List<String>,
        tradeItemTypes: List<String>,
        tradePrices: List<String>,
        tradePointsMultiplier: List<String>,
        tradeQuantityLabel: List<String>
    ): Response<Int> {
        return dumpyService.saveTradesList(
            establishmentID,
            tradeItems,
            tradeItemTypes,
            tradePrices,
            tradePointsMultiplier,
            tradeQuantityLabel
        )
    }

    suspend fun changeOwnerPic(
        ownerID: Int,
        ownerEmail: String,
        ownerPic: MultipartBody.Part //userFile
    ): Response<Int> {
        return dumpyService.changeOwnerPic(
            ownerID, ownerEmail, ownerPic
        )
    }

    suspend fun checkTradeProposalStatus(
        establishmentID: Int
    ): Response<Int> {
        return dumpyService.checkTradeProposalStatus(
            establishmentID
        )
    }

    suspend fun getReport(
        ownerID: Int,
        establishmentID: Int
    ): Response<ResponseBody> {
        return dumpyService.getReport(
            ownerID, establishmentID
        )
    }
}