package com.envizioners.dumpy.ownerclient.viewmodels.recycler

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.envizioners.dumpy.ownerclient.repository.RecyclerRepository
import kotlinx.coroutines.launch

class RecyclerTradeProposalActionVM(application: Application): AndroidViewModel(application) {

    val repository = RecyclerRepository(application)
    var actionResponseMLD = MutableLiveData<String>()
    var ratingMLD = MutableLiveData<Int>()

    fun cancelTrade(
        userID: Int,
        tradeProposalID: Int
    ) {
        viewModelScope.launch {
            val response = repository.cancelTrade(
                userID,
                tradeProposalID
            )
            actionResponseMLD.postValue(response)
        }
    }

    fun removeTrade(
        userID: Int,
        tradeProposalID: Int,
        userEmail: String
    ){
        viewModelScope.launch {
            val response = repository.deleteTrade(
                userID,
                tradeProposalID,
                userEmail
            )
            actionResponseMLD.postValue(response)
        }
    }

    fun proceedTrade(
        userID: Int,
        tradeProposalID: Int,
        establishmentID: Int,
        transactionMode: String,
        couponName: String?
    ){
        viewModelScope.launch {
            val response = repository.proceedTrade(
                userID,
                tradeProposalID,
                establishmentID,
                transactionMode,
                couponName
            )
            actionResponseMLD.postValue(response)
        }
    }

    fun insertRating(
        userID: Int,
        tradeProposalID: Int,
        rating: Float
    ){
        viewModelScope.launch {
            val response = repository.insertRating(
                userID,
                tradeProposalID,
                rating
            )
            ratingMLD.postValue(response)
        }
    }
}