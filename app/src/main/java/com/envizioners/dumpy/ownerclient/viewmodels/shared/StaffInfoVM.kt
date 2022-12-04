package com.envizioners.dumpy.ownerclient.viewmodels.shared

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.envizioners.dumpy.ownerclient.repository.SharedRepository
import com.envizioners.dumpy.ownerclient.response.shared.StaffInfo
import kotlinx.coroutines.launch

class StaffInfoVM(application: Application): AndroidViewModel(application) {

    val repository = SharedRepository(application)
    private val _staffInfoMLD = MutableLiveData<StaffInfo>()
    val staffInfoLD: LiveData<StaffInfo> = _staffInfoMLD

    fun getStaffInfo(
        staffID: Int,
        ownerID: Int
    ){
        viewModelScope.launch {
            val response = repository.getStaffInfo(
                staffID,
                ownerID
            )
            if (response != null){
                _staffInfoMLD.postValue(response!!)
            }
        }
    }
}