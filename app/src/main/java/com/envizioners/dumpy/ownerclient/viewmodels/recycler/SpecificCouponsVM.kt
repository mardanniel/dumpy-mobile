package com.envizioners.dumpy.ownerclient.viewmodels.recycler

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.envizioners.dumpy.ownerclient.repository.RecyclerRepository
import com.envizioners.dumpy.ownerclient.response.points.SpecificCoupons
import kotlinx.coroutines.launch

class SpecificCouponsVM(application: Application): AndroidViewModel(application) {

    val repository = RecyclerRepository(application)
    private val _scMLD = MutableLiveData<SpecificCoupons>()
    val scLD: LiveData<SpecificCoupons> = _scMLD

    fun getSpecificCoupons(
        userID: Int,
        establishmentID: Int
    ){
        viewModelScope.launch {
            val response = repository.getSpecificCoupons(
                userID,
                establishmentID
            )
            _scMLD.postValue(response)
        }
    }
}