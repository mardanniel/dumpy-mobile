package com.envizioners.dumpy.ownerclient.viewmodels.owner

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.envizioners.dumpy.ownerclient.repository.OwnerRepository
import kotlinx.coroutines.launch

class SalesVM(application: Application): AndroidViewModel(application) {

    val repository = OwnerRepository(application)
    var responseFile = MutableLiveData<Any>()

    fun getReport(ownerID: Int, establishmentID: Int){
        viewModelScope.launch {
            var response = repository.getReport(
                ownerID, establishmentID
            )
            responseFile.postValue(response)
        }
    }
}