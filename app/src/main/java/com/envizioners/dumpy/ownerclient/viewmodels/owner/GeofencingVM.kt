package com.envizioners.dumpy.ownerclient.viewmodels.owner

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.envizioners.dumpy.ownerclient.repository.OwnerRepository
import com.envizioners.dumpy.ownerclient.response.owner_main.EstablishmentLocation
import com.envizioners.dumpy.ownerclient.response.validation.FormValidationResponse
import kotlinx.coroutines.launch

class GeofencingVM(application: Application): AndroidViewModel(application) {

    private val repository = OwnerRepository(application)
    val getEL = MutableLiveData<EstablishmentLocation>()
    val saveEL = MutableLiveData<Int>()

    fun getEstablishmentLocation(junkshopOwnerID: Int){
        viewModelScope.launch {
            var response = repository.getEstablishmentLocation(junkshopOwnerID)
            getEL.postValue(response!!)
        }
    }

    fun saveEstablishmentLocation(
        establishmentID: Int,
        latitude: Double,
        longitude: Double
    ){
        viewModelScope.launch {
            var response = repository.saveEstablishmentLocation(
                establishmentID,
                latitude,
                longitude
            )
            saveEL.postValue(response)
        }
    }
}