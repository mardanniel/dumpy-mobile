package com.envizioners.dumpy.ownerclient.viewmodels.owner

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.envizioners.dumpy.ownerclient.repository.OwnerRepository
import com.envizioners.dumpy.ownerclient.response.points.ExchangeRequest
import kotlinx.coroutines.launch

class ExchangeRequestListVM(application: Application): AndroidViewModel(application) {

    val repository = OwnerRepository(application)
    var erlMLD = MutableLiveData<List<ExchangeRequest>>()
    var acceptER = MutableLiveData<Int>()

    fun getRequestList(
        establishmentID: Int
    ) {
        viewModelScope.launch {
            val response = repository.getRequestList(
                establishmentID
            )
            erlMLD.postValue(response)
        }
    }

    fun acceptExchangeRequest(
        exchangeRequestID: Int
    ) {
        viewModelScope.launch {
            val response = repository.acceptExchangeRequest(
                exchangeRequestID
            )
            acceptER.postValue(response)
        }
    }
}