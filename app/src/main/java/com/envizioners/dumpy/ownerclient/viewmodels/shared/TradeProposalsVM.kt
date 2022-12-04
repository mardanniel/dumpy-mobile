package com.envizioners.dumpy.ownerclient.viewmodels.shared

import android.app.Application
import androidx.lifecycle.*
import com.envizioners.dumpy.ownerclient.repository.OwnerRepository
import com.envizioners.dumpy.ownerclient.response.tradeproposal.TradeProposal
import kotlinx.coroutines.launch

class TradeProposalsVM(application: Application): AndroidViewModel(application) {

    private val repository = OwnerRepository(application)
    private val _tradeProposalsMutableLiveData = MutableLiveData<TradeProposal>()
    val tradeProposalsLiveData: LiveData<TradeProposal> = _tradeProposalsMutableLiveData

    fun getTradeProposals(junkshopID: Int){
        viewModelScope.launch {
            val response = repository.getTradeProposals(junkshopID)
            _tradeProposalsMutableLiveData.postValue(response!!)
        }
    }
}