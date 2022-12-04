package com.envizioners.dumpy.ownerclient.model_implementations

import com.envizioners.dumpy.ownerclient.model_interfaces.DumpyRecyclerService
import com.envizioners.dumpy.ownerclient.repository.BaseRepository
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
import retrofit2.http.Field

class RecyclerServiceSource (
    private val dumpyService: DumpyRecyclerService
) {
    suspend fun authenticateRecycler(
        userEmail: String,
        userPassword: String
    ): Response<AuthRecyclerLogin> {
        return dumpyService.authenticateRecycler(
            userEmail,
            userPassword
        )
    }

    suspend fun registerRecycler(
        email: String,
        fname: String,
        mname: String,
        lname: String,
        contact: String,
        birthdate: String,
        street: String,
        barangay: String,
        city: String,
        password: String,
        confPass: String,
        agree: String
    ): Response<AuthRecyclerRegister> {
        return dumpyService.registerRecycler(
            email,
            fname,
            mname,
            lname,
            contact,
            birthdate,
            street,
            barangay,
            city,
            password,
            confPass,
            agree
        )
    }

    suspend fun getRecyclerInfo(
        userID: Int
    ): Response<RecyclerProfile> {
        return dumpyService.getRecyclerInfo(
            userID
        )
    }

    suspend fun getTradeProposalStatuses(
        userID: Int
    ): Response<TradeProposalCountByStatus> {
        return dumpyService.getTradeProposalsStatus(
            userID
        )
    }

    suspend fun insertTrade(
        tradeItemImages: List<MultipartBody.Part>,
        tradeItems: String,
        tradeItemQuantities: String,
        tradeMeasurements: String,
        junkshopID: Int,
        userEmail: String,
        userID: Int
    ): Response<FormValidationResponse> {
        return dumpyService.insertTrade(
            tradeItemImages,
            tradeItems,
            tradeItemQuantities,
            tradeMeasurements,
            junkshopID,
            userEmail,
            userID
        )
    }

    suspend fun getTradesList(
        junkshopID: Int
    ): Response<TradesLists> {
        return dumpyService.getTradeList(
            junkshopID
        )
    }

    suspend fun getNearestEstablishments(
        lat: Double,
        lng: Double
    ): Response<EstablishmentLoc> {
        return dumpyService.getNearestEstablishments(
            lat,
            lng
        )
    }

    suspend fun getConversationsList(
        recyclerID: Int
    ): Response<ChatMenuRList> {
        return dumpyService.getConversationList(
            recyclerID
        )
    }

    suspend fun getConversationToken(
        chat_sender: Int,
        chat_receiver: Int
    ): Response<String> {
        return dumpyService.getConversationToken(
            chat_sender,
            chat_receiver
        )
    }

    suspend fun getTradeProposals(
        userID: Int,
        tpStatusCode: Int
    ): Response<TradeProposalsR> {
        return dumpyService.getTradeProposals(
            userID,
            tpStatusCode
        )
    }

    suspend fun getExchangePointsList(
        userID: Int
    ): Response<ExchangePointsResult> {
        return dumpyService.getExchangePointsList(
            userID
        )
    }

    suspend fun getCouponsList(
        userID: Int
    ): Response<CouponsResult> {
        return dumpyService.getCouponsList(
            userID
        )
    }

    suspend fun sendRequest(
        userID: Int,
        establishmentID: Int
    ): Response<Int> {
        return dumpyService.sendRequest(
            userID,
            establishmentID
        )
    }

    suspend fun getSpecificCoupons(
        userID: Int,
        establishmentID: Int
    ): Response<SpecificCoupons> {
        return dumpyService.getSpecificCoupons(
            userID,
            establishmentID
        )
    }

    suspend fun cancelTrade(
        userID: Int,
        tradeProposalID: Int
    ): Response<String> {
        return dumpyService.cancelTrade(
            userID,
            tradeProposalID
        )
    }

    suspend fun deleteTrade(
        userID: Int,
        tradeProposalID: Int,
        userEmail: String
    ): Response<String> {
        return dumpyService.deleteTrade(
            userID,
            tradeProposalID,
            userEmail
        )
    }

    suspend fun proceedTrade(
        userID: Int,
        tradeProposalID: Int,
        establishmentID: Int,
        transactionMode: String,
        couponName: String?
    ): Response<String> {
        return dumpyService.proceedTrade(
            userID,
            tradeProposalID,
            establishmentID,
            transactionMode,
            couponName
        )
    }

    suspend fun insertRating(
        userID: Int,
        tradeProposalID: Int,
        rating: Float
    ): Response<Int> {
        return dumpyService.insertRating(
            userID,
            tradeProposalID,
            rating
        )
    }

    suspend fun changeRecyclerPic(
        recyclerID: Int,
        recyclerPic: MultipartBody.Part //userFile
    ): Response<Int> {
        return dumpyService.changeRecyclerPic(
            recyclerID,
            recyclerPic
        )
    }
}