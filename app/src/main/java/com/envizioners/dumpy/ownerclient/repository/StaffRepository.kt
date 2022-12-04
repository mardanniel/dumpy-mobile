package com.envizioners.dumpy.ownerclient.repository

import android.content.Context
import com.envizioners.dumpy.ownerclient.network.StaffNetwork
import com.envizioners.dumpy.ownerclient.response.authrecycler.AuthRecyclerLogin
import com.envizioners.dumpy.ownerclient.response.authstaff.AuthStaffLogin
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class StaffRepository(
    context: Context
) {
    private val passedContext = context

    suspend fun authenticateStaff(
        userEmail: String,
        userPassword: String
    ): AuthStaffLogin? {
        val request = StaffNetwork(passedContext).staffClient.authenticateStaff(
            userEmail,
            userPassword
        )
        return if(!request.isSuccessful){
            var gson = Gson()
            val type = object : TypeToken<AuthStaffLogin>() {}.type
            var errorResponse: AuthStaffLogin = gson.fromJson(request.errorBody()!!.charStream(), type)
            errorResponse
        }else{
            request.body()!!
        }
    }

    suspend fun decideTrade(
        tradeProposalID: Int,
        tpMessage: String,
        staffID: Int,
        decision: Int
    ): String {
        val request = StaffNetwork(passedContext).staffClient.decideTrade(
            tradeProposalID,
            tpMessage,
            staffID,
            decision
        )
        return if(!request.isSuccessful){
            var gson = Gson()
            val type = object : TypeToken<String>() {}.type
            var errorResponse: String = gson.fromJson(request.errorBody()!!.charStream(), type)
            errorResponse
        }else{
            request.body()!!
        }
    }

    suspend fun concludeTrade(
        tradeProposalID: Int,
        tradeQuantities: List<Int>
    ): String? {
        val request = StaffNetwork(passedContext).staffClient.concludeTrade(
            tradeProposalID,
            tradeQuantities
        )
        return if(!request.isSuccessful){
            var gson = Gson()
            val type = object : TypeToken<String>() {}.type
            var errorResponse: String = gson.fromJson(request.errorBody()!!.charStream(), type)
            errorResponse
        }else{
            request.body()!!
        }
    }

}