package com.envizioners.dumpy.ownerclient.viewmodels.staff

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.envizioners.dumpy.ownerclient.repository.StaffRepository
import kotlinx.coroutines.launch

class StaffTPActionsVM(application: Application): AndroidViewModel(application) {

    val repository = StaffRepository(application)
    val _responseMLD = MutableLiveData<String>()

    fun concludeTrade(
        tradeProposalID: Int,
        tradeQuantities: List<Int>
    ) {
        viewModelScope.launch {
            val response = repository.concludeTrade(
                tradeProposalID,
                tradeQuantities
            )
            _responseMLD.postValue(response!!)
        }
    }

    fun decideTrade(
        tradeProposalID: Int,
        tpMessage: String,
        staffID: Int,
        decision: Int
    ) {
        viewModelScope.launch {
            val response = repository.decideTrade(
                tradeProposalID,
                tpMessage,
                staffID,
                decision
            )
            _responseMLD.postValue(response)
        }
    }
}