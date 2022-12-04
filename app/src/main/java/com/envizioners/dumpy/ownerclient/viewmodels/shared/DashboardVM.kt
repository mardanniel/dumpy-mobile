package com.envizioners.dumpy.ownerclient.viewmodels.shared

import android.app.Application
import androidx.lifecycle.*
import com.envizioners.dumpy.ownerclient.repository.SharedRepository
import com.envizioners.dumpy.ownerclient.response.shared.TradeProposalCountByStatus
import com.envizioners.dumpy.ownerclient.response.tradeproposal.TradeProposalsStatus
import kotlinx.coroutines.launch

class DashboardVM(application: Application): AndroidViewModel(application) {

    private val repository = SharedRepository(application)
    private val _tradeProposalsStatusMutableLiveData = MutableLiveData<TradeProposalCountByStatus>()
    val tradeProposalsStatusLiveData: LiveData<TradeProposalCountByStatus> = _tradeProposalsStatusMutableLiveData

    fun getTpStatuses(userID: Int, userType: String){
        viewModelScope.launch {
            var response = if (userType == "DUMPY_RECYCLER"){
                repository.getTradeProposalStatusesR(userID)
            }else{
                repository.getTradeProposalsStatusJS(userID)
            }
            _tradeProposalsStatusMutableLiveData.postValue(response)
        }
    }
}

