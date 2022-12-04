package com.envizioners.dumpy.ownerclient.viewmodels.owner

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.envizioners.dumpy.ownerclient.repository.OwnerRepository
import com.envizioners.dumpy.ownerclient.response.owner_main.OwnerProfile
import kotlinx.coroutines.launch

class OwnerProfileVM(application: Application): AndroidViewModel(application)  {

    val repository = OwnerRepository(application)
    val profileResponse = MutableLiveData<OwnerProfile>()
    val ctps = MutableLiveData<Int>()

    fun getProfileDetails(ownerId: Int){
        viewModelScope.launch {
            val response = repository.getProfileDetails(ownerId)
            profileResponse.postValue(response)
        }
    }

    fun checkTradeProposalStatus(establishmentID: Int){
        viewModelScope.launch {
            val response =  repository.checkTradeProposalStatus(establishmentID)
            ctps.postValue(response)
        }
    }

}