package com.envizioners.dumpy.ownerclient.repository

import android.content.Context
import android.util.Log
import com.envizioners.dumpy.ownerclient.network.OwnerNetwork
import com.envizioners.dumpy.ownerclient.network.RecyclerNetwork
import com.envizioners.dumpy.ownerclient.network.SharedNetwork
import com.envizioners.dumpy.ownerclient.network.StaffNetwork
import com.envizioners.dumpy.ownerclient.response.shared.*
import com.envizioners.dumpy.ownerclient.response.tradeproposal.TradeProposalResult
import com.envizioners.dumpy.ownerclient.response.tradeproposal.TradeProposalsStatus
import com.envizioners.dumpy.ownerclient.response.validation.FormValidationResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.MultipartBody

class SharedRepository(
    context: Context
) {
    private val passedContext = context

    suspend fun getRecyclerConversationList(
        recyclerID: Int
    ): ChatMenuRList? {
        val request = RecyclerNetwork(passedContext).recyclerClient.getConversationsList(
            recyclerID
        )
        return if(!request.isSuccessful){
            var gson = Gson()
            val type = object : TypeToken<ChatMenuRList>() {}.type
            var errorResponse: ChatMenuRList = gson.fromJson(
                request.errorBody()!!.charStream(),
                type
            )
            errorResponse
        }else{
            request.body()!!
        }
    }

    suspend fun getJunkshopConversationList(junkshopID: Int): ChatMenuJList? {
        val request = OwnerNetwork(passedContext).ownerClient.getConversationList(junkshopID)
        return if(!request.isSuccessful){
            var gson = Gson()
            val type = object : TypeToken<ChatMenuJList?>() {}.type
            var errorResponse: ChatMenuJList? = gson.fromJson(request.errorBody()!!.charStream(), type)
            errorResponse
        }else {
            request.body()!!
        }
    }

    suspend fun getConversationToken(chat_sender: Int, chat_receiver: Int): String? {
        val request = RecyclerNetwork(passedContext).recyclerClient.getConversationToken(
            chat_sender,
            chat_receiver
        )
        return if(!request.isSuccessful){
            var gson = Gson()
            val type = object : TypeToken<String>() {}.type
            var errorResponse: String? = gson.fromJson(request.errorBody()!!.charStream(), type)
            errorResponse
        }else {
            request.body()!!
        }
    }

    suspend fun getTradeProposalsStatusJS(
        junkshopOwnerId: Int
    ): TradeProposalCountByStatus {
        val request = SharedNetwork(passedContext).sharedClient.getTradeProposalStatuses(
            junkshopOwnerId
        )
        return if(!request.isSuccessful){
            var gson = Gson()
            val type = object : TypeToken<TradeProposalCountByStatus>() {}.type
            var errorResponse: TradeProposalCountByStatus = gson.fromJson(request.errorBody()!!.charStream(), type)
            errorResponse
        }else {
            request.body()!!
        }
    }

    suspend fun getTradeProposalStatusesR(
        userID: Int
    ): TradeProposalCountByStatus {
        val request = RecyclerNetwork(passedContext).recyclerClient.getTradeProposalStatuses(
            userID
        )
        return if(!request.isSuccessful){
            var gson = Gson()
            val type = object : TypeToken<TradeProposalCountByStatus>() {}.type
            var errorResponse: TradeProposalCountByStatus = gson.fromJson(
                request.errorBody()!!.charStream(),
                type
            )
            errorResponse
        }else{
            request.body()!!
        }
    }

    suspend fun getLeaderboard(): LeaderboardPlacerResult? {
        val request = SharedNetwork(passedContext).sharedClient.getLeaderboard()
        return if(!request.isSuccessful){
            var gson = Gson()
            val type = object : TypeToken<LeaderboardPlacerResult>() {}.type
            var errorResponse: LeaderboardPlacerResult = gson.fromJson(
                request.errorBody()!!.charStream(),
                type
            )
            errorResponse
        }else{
            request.body()!!
        }
    }

    suspend fun insertReport(
        source_id: Int,
        source_role: String,
        report_text: String,
        report_type: String,
        report_image: MultipartBody.Part?
    ): FormValidationResponse? {
        val request = SharedNetwork(passedContext).sharedClient.insertReport(
            source_id,
            source_role,
            report_text,
            report_type,
            report_image
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

    suspend fun setUDT(
        userID: Int,
        token: String,
        userRole: String
    ): Boolean {
        val request = SharedNetwork(passedContext).sharedClient.setUDT(
            userID,
            token,
            userRole
        )
        return if(!request.isSuccessful){
            var gson = Gson()
            val type = object : TypeToken<Boolean>() {}.type
            var errorResponse: Boolean = gson.fromJson(
                request.errorBody()!!.charStream(),
                type
            )
            errorResponse
        }else{
            request.body()!!
        }
    }

    suspend fun unsetUDT(
        userID: Int,
        userRole: String
    ): Boolean {
        val request = SharedNetwork(passedContext).sharedClient.unsetUDT(
            userID,
            userRole
        )
        return if(!request.isSuccessful){
            var gson = Gson()
            val type = object : TypeToken<Boolean>() {}.type
            var errorResponse: Boolean = gson.fromJson(
                request.errorBody()!!.charStream(),
                type
            )
            errorResponse
        }else{
            request.body()!!
        }
    }

    suspend fun getStaffInfo(
        staffID: Int,
        ownerID: Int
    ): StaffInfo? {
        val request = SharedNetwork(passedContext).sharedClient.getStaffInfo(
            staffID,
            ownerID
        )
        return if(!request.isSuccessful){
            null
        }else{
            request.body()!!
        }
    }

    suspend fun getPendingProposals(
        establishmentID: Int
    ): TradeProposalResult? {
        val request = SharedNetwork(passedContext).sharedClient.getPendingProposals(
            establishmentID
        )
        return if(!request.isSuccessful){
            var gson = Gson()
            val type = object : TypeToken<TradeProposalResult>() {}.type
            var errorResponse: TradeProposalResult = gson.fromJson(
                request.errorBody()!!.charStream(),
                type
            )
            errorResponse
        }else{
            request.body()!!
        }
    }

    suspend fun getProceededProposals(
        establishmentID: Int
    ): TradeProposalResult? {
        val request = SharedNetwork(passedContext).sharedClient.getProceededProposals(
            establishmentID
        )
        return if(!request.isSuccessful){
            var gson = Gson()
            val type = object : TypeToken<TradeProposalResult>() {}.type
            var errorResponse: TradeProposalResult = gson.fromJson(
                request.errorBody()!!.charStream(),
                type
            )
            errorResponse
        }else{
            request.body()!!
        }
    }

    suspend fun changeOwnerPic(
        ownerID: Int,
        ownerEmail: String,
        ownerPic: MultipartBody.Part
    ): Int {
        val request = OwnerNetwork(passedContext).ownerClient.changeOwnerPic(
            ownerID, ownerEmail, ownerPic
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

    suspend fun changeStaffPic(
        staffID: Int,
        staffEmail: String,
        staffPic: MultipartBody.Part
    ): Int {
        val request = StaffNetwork(passedContext).staffClient.changeStaffPic(
            staffID, staffEmail, staffPic
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

    suspend fun changeRecyclerPic(
        recyclerID: Int,
        recyclerPic: MultipartBody.Part
    ): Int {
        val request = RecyclerNetwork(passedContext).recyclerClient.changeRecyclerPic(
            recyclerID, recyclerPic
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