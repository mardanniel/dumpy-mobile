package com.envizioners.dumpy.ownerclient.viewmodels.recycler

import android.app.Application
import androidx.lifecycle.*
import com.envizioners.dumpy.ownerclient.repository.RecyclerRepository
import com.envizioners.dumpy.ownerclient.response.recycler_main.EstablishmentsLocResultBody
import kotlinx.coroutines.launch

class SearchJunkshopVM(application: Application) : AndroidViewModel(application) {

    private val repository = RecyclerRepository(application)
    private val _searchMLD = MutableLiveData<List<EstablishmentsLocResultBody>>()
    val searchLD: LiveData<List<EstablishmentsLocResultBody>> = _searchMLD

    fun getNearest(
        lat: Double,
        lng: Double
    ) {
        viewModelScope.launch {
            val response = repository.getNearestEstablishments(lat, lng)
            _searchMLD.postValue(response?.result)
        }
    }
}