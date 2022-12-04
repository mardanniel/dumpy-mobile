package com.envizioners.dumpy.ownerclient.viewmodels.recycler

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.envizioners.dumpy.ownerclient.repository.RecyclerRepository
import com.envizioners.dumpy.ownerclient.response.recycler_main.TradeProposalsRResultBody
import kotlinx.coroutines.launch

class TradeProposalsVM(application: Application): AndroidViewModel(application) {

    val repository = RecyclerRepository(application)
    val tpmld = MutableLiveData<List<TradeProposalsRResultBody>>()

    fun getTradeProposals(
        userID: Int,
        tpStatusCode: Int
    ){
        viewModelScope.launch {
            val response = repository.getTradeProposals(
                userID,
                tpStatusCode
            )
            tpmld.postValue(response)
        }
    }
}