package com.envizioners.dumpy.ownerclient.repository

import android.content.Context
import com.envizioners.dumpy.ownerclient.network.RecyclerNetwork
import com.envizioners.dumpy.ownerclient.response.authrecycler.AuthRecyclerLogin
import com.envizioners.dumpy.ownerclient.response.authrecycler.AuthRecyclerRegister
import com.envizioners.dumpy.ownerclient.response.points.CouponsResult
import com.envizioners.dumpy.ownerclient.response.points.ExchangePointsResult
import com.envizioners.dumpy.ownerclient.response.points.SpecificCoupons
import com.envizioners.dumpy.ownerclient.response.recycler_main.EstablishmentLoc
import com.envizioners.dumpy.ownerclient.response.recycler_main.RecyclerProfile
import com.envizioners.dumpy.ownerclient.response.recycler_main.TradeProposalsR
import com.envizioners.dumpy.ownerclient.response.tradeproposal.TradesLists
import com.envizioners.dumpy.ownerclient.response.validation.FormValidationResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.MultipartBody
import retrofit2.Response

class RecyclerRepository(
    context: Context
) {
    private val passedContext = context

    suspend fun authenticateRecycler(
        userEmail: String,
        userPassword: String
    ): AuthRecyclerLogin? {
        val request = RecyclerNetwork(passedContext).recyclerClient.authenticateRecycler(
            userEmail,
            userPassword
        )

        return if(!request.isSuccessful){
            var gson = Gson()
            val type = object : TypeToken<AuthRecyclerLogin>() {}.type
            var errorResponse: AuthRecyclerLogin = gson.fromJson(request.errorBody()!!.charStream(), type)
            errorResponse
        }else{
            request.body()!!
        }
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
    ): AuthRecyclerRegister {
        val request = RecyclerNetwork(passedContext).recyclerClient.registerRecycler(
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
        return if(!request.isSuccessful){
            var gson = Gson()
            val type = object : TypeToken<AuthRecyclerRegister>() {}.type
            var errorResponse: AuthRecyclerRegister = gson.fromJson(request.errorBody()!!.charStream(), type)
            errorResponse
        }else{
            request.body()!!
        }
    }

    suspend fun getInfo(
        userID: Int
    ): RecyclerProfile {
        val request = RecyclerNetwork(passedContext).recyclerClient.getRecyclerInfo(
            userID
        )
        return if(!request.isSuccessful){
            var gson = Gson()
            val type = object : TypeToken<RecyclerProfile>() {}.type
            var errorResponse: RecyclerProfile = gson.fromJson(request.errorBody()!!.charStream(), type)
            errorResponse
        }else{
            request.body()!!
        }
    }

    suspend fun insertTrade(
        tradeItemImages: List<MultipartBody.Part>,
        tradeItems: String,
        tradeItemQuantities: String,
        tradeMeasurements: String,
        junkshopID: Int,
        userEmail: String,
        userID: Int
    ): FormValidationResponse? {
        val request = RecyclerNetwork(passedContext).recyclerClient.insertTrade(
            tradeItemImages,
            tradeItems,
            tradeItemQuantities,
            tradeMeasurements,
            junkshopID,
            userEmail,
            userID
        )
        return if(!request.isSuccessful){
            var gson = Gson()
            val type = object : TypeToken<FormValidationResponse>() {}.type
            var errorResponse: FormValidationResponse = gson.fromJson(
                request.errorBody()!!.charStream(),
                type
            )
            errorResponse
        }else{
            request.body()!!
        }
    }

    suspend fun getTradesList(
        junkshopID: Int
    ): TradesLists? {
        val request = RecyclerNetwork(passedContext).recyclerClient.getTradesList(
            junkshopID
        )
        return if(!request.isSuccessful){
            var gson = Gson()
            val type = object : TypeToken<TradesLists>() {}.type
            var errorResponse: TradesLists = gson.fromJson(
                request.errorBody()!!.charStream(),
                type
            )
            errorResponse
        }else{
            request.body()!!
        }
    }

    suspend fun getNearestEstablishments(
        lat: Double,
        lng: Double
    ): EstablishmentLoc? {
        val request = RecyclerNetwork(passedContext).recyclerClient.getNearestEstablishments(
            lat,
            lng
        )
        return if(!request.isSuccessful){
            var gson = Gson()
            val type = object : TypeToken<EstablishmentLoc>() {}.type
            var errorResponse: EstablishmentLoc = gson.fromJson(
                request.errorBody()!!.charStream(),
                type
            )
            errorResponse
        }else{
            request.body()!!
        }
    }

    suspend fun getTradeProposals(
        userID: Int,
        tpStatusCode: Int
    ): TradeProposalsR {
        val request = RecyclerNetwork(passedContext).recyclerClient.getTradeProposals(
            userID,
            tpStatusCode
        )
        return if(!request.isSuccessful){
            var gson = Gson()
            val type = object : TypeToken<TradeProposalsR>() {}.type
            var errorResponse: TradeProposalsR = gson.fromJson(
                request.errorBody()!!.charStream(),
                type
            )
            errorResponse
        }else{
            request.body()!!
        }
    }

    suspend fun getExchangePointsList(
        userID: Int
    ): ExchangePointsResult? {
        val request = RecyclerNetwork(passedContext).recyclerClient.getExchangePointsList(
            userID
        )
        return if(!request.isSuccessful){
            var gson = Gson()
            val type = object : TypeToken<ExchangePointsResult>() {}.type
            var errorResponse: ExchangePointsResult = gson.fromJson(
                request.errorBody()!!.charStream(),
                type
            )
            errorResponse
        }else{
            request.body()!!
        }
    }

    suspend fun getCouponsList(
        userID: Int
    ): CouponsResult? {
        val request = RecyclerNetwork(passedContext).recyclerClient.getCouponsList(
            userID
        )
        return if(!request.isSuccessful){
            var gson = Gson()
            val type = object : TypeToken<CouponsResult>() {}.type
            var errorResponse: CouponsResult = gson.fromJson(
                request.errorBody()!!.charStream(),
                type
            )
            errorResponse
        }else{
            request.body()!!
        }
    }

    suspend fun sendRequest(
        userID: Int,
        establishmentID: Int
    ): Int? {
        val request = RecyclerNetwork(passedContext).recyclerClient.sendRequest(
            userID,
            establishmentID
        )
        return if(!request.isSuccessful){
            var gson = Gson()
            val type = object : TypeToken<Int>() {}.type
            var errorResponse: Int = gson.fromJson(
                request.errorBody()!!.charStream(),
                type
            )
            errorResponse
        }else{
            request.body()
        }
    }

    suspend fun getSpecificCoupons(
        userID: Int,
        establishmentID: Int
    ): SpecificCoupons {
        val request = RecyclerNetwork(passedContext).recyclerClient.getSpecificCoupons(
            userID,
            establishmentID
        )
        return if(!request.isSuccessful){
            var gson = Gson()
            val type = object : TypeToken<SpecificCoupons>() {}.type
            var errorResponse: SpecificCoupons = gson.fromJson(
                request.errorBody()!!.charStream(),
                type
            )
            errorResponse
        }else{
            request.body()!!
        }
    }

    suspend fun cancelTrade(
        userID: Int,
        tradeProposalID: Int
    ): String {
        val request = RecyclerNetwork(passedContext).recyclerClient.cancelTrade(
            userID,
            tradeProposalID
        )
        return if(!request.isSuccessful){
            var gson = Gson()
            val type = object : TypeToken<String>() {}.type
            var errorResponse: String = gson.fromJson(
                request.errorBody()!!.charStream(),
                type
            )
            errorResponse.toString()
        }else{
            request.body().toString()
        }
    }

    suspend fun deleteTrade(
        userID: Int,
        tradeProposalID: Int,
        userEmail: String
    ): String {
        val request = RecyclerNetwork(passedContext).recyclerClient.deleteTrade(
            userID,
            tradeProposalID,
            userEmail
        )
        return if(!request.isSuccessful){
            var gson = Gson()
            val type = object : TypeToken<String>() {}.type
            var errorResponse: String = gson.fromJson(
                request.errorBody()!!.charStream(),
                type
            )
            errorResponse.toString()
        }else{
            request.body().toString()!!
        }
    }

    suspend fun proceedTrade(
        userID: Int,
        tradeProposalID: Int,
        establishmentID: Int,
        transactionMode: String,
        couponName: String?
    ): String {
        val request = RecyclerNetwork(passedContext).recyclerClient.proceedTrade(
            userID,
            tradeProposalID,
            establishmentID,
            transactionMode,
            couponName
        )
        return if(!request.isSuccessful){
            var gson = Gson()
            val type = object : TypeToken<String>() {}.type
            var errorResponse: String = gson.fromJson(
                request.errorBody()!!.charStream(),
                type
            )
            errorResponse
        }else{
            request.body().toString()!!
        }
    }

    suspend fun insertRating(
        userID: Int,
        transactionID: Int,
        rating: Float
    ): Int {
        val request = RecyclerNetwork(passedContext).recyclerClient.insertRating(
            userID,
            transactionID,
            rating
        )
        return if(!request.isSuccessful){
            var gson = Gson()
            val type = object : TypeToken<Int>() {}.type
            var errorResponse: Int = gson.fromJson(
                request.errorBody()!!.charStream(),
                type
            )
            errorResponse
        }else{
            request.body()!!
        }
    }
}