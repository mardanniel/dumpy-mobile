package com.envizioners.dumpy.ownerclient.viewmodels.owner

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.envizioners.dumpy.ownerclient.repository.OwnerRepository
import kotlinx.coroutines.launch

class TradesListVM(application: Application): AndroidViewModel(application) {

    val repository = OwnerRepository(application)
    val resp = MutableLiveData<Int>()

    fun saveTradesList(
        establishmentID: Int,
        tradeItems: List<String>,
        tradeItemTypes: List<String>,
        tradePrices: List<String>,
        tradePointsMultiplier: List<String>,
        tradeQuantityLabel: List<String>
    ){
        viewModelScope.launch {
            val response = repository.saveTradesList(
                establishmentID,
                tradeItems,
                tradeItemTypes,
                tradePrices,
                tradePointsMultiplier,
                tradeQuantityLabel
            )
            resp.postValue(response)
        }
    }
}