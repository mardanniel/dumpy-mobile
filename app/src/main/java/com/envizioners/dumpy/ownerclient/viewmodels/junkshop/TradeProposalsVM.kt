package com.envizioners.dumpy.ownerclient.viewmodels.junkshop

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.envizioners.dumpy.ownerclient.repository.SharedRepository
import com.envizioners.dumpy.ownerclient.response.tradeproposal.TradeProposalResult
import kotlinx.coroutines.launch

class TradeProposalsVM(application: Application): AndroidViewModel(application) {

    val repository = SharedRepository(application)
    private var _ppMLD = MutableLiveData<TradeProposalResult>()
    val ppLD: LiveData<TradeProposalResult> = _ppMLD

    fun getPendingProposals(
        establishmentID: Int
    ){
        viewModelScope.launch {
            val response = repository.getPendingProposals(
                establishmentID
            )
            _ppMLD.postValue(response!!)
        }
    }

    fun getProceededProposals(
        establishmentID: Int
    ){
        viewModelScope.launch {
            val response = repository.getProceededProposals(
                establishmentID
            )
            _ppMLD.postValue(response!!)
        }
    }
}