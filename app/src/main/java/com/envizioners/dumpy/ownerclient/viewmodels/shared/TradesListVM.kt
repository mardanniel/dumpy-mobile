package com.envizioners.dumpy.ownerclient.viewmodels.shared

import android.app.Application
import androidx.lifecycle.*
import com.envizioners.dumpy.ownerclient.repository.RecyclerRepository
import com.envizioners.dumpy.ownerclient.response.tradeproposal.TradesLists
import kotlinx.coroutines.launch

class TradesListVM(application: Application): AndroidViewModel(application) {

    private val repository = RecyclerRepository(application)
    private val _tradesListMLD = MutableLiveData<TradesLists>()
    val tradesListLD: LiveData<TradesLists> = _tradesListMLD

    fun getTradesList(junkshopID: Int){
        viewModelScope.launch {
            val response = repository.getTradesList(junkshopID)
            _tradesListMLD.postValue(response!!)
        }
    }
}