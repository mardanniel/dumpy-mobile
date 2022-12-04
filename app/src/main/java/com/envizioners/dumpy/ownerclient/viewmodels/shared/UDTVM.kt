package com.envizioners.dumpy.ownerclient.viewmodels.shared

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.envizioners.dumpy.ownerclient.repository.SharedRepository
import kotlinx.coroutines.launch

class UDTVM(application: Application): AndroidViewModel(application) {

    val repository = SharedRepository(application)
    var isSet = MutableLiveData<Boolean>()
    var isUnset = MutableLiveData<Boolean>()

    fun setUDT(
        userID: Int,
        token: String,
        userRole: String
    ){
        viewModelScope.launch {
            val response = repository.setUDT(
                userID,
                token,
                userRole
            )
            isSet.postValue(response)
        }
    }
    fun unsetUDT(
        userID: Int,
        userRole: String
    ){
        viewModelScope.launch {
            val response = repository.unsetUDT(
                userID,
                userRole
            )
            isUnset.postValue(response)
        }
    }
}