package com.envizioners.dumpy.ownerclient.viewmodels.owner

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.envizioners.dumpy.ownerclient.repository.OwnerRepository
import kotlinx.coroutines.launch

class OwnerActionsVM(application: Application): AndroidViewModel(application) {

    private val TOGGLE_STATUS = 5
    private val REMOVE_STAFF = 6

    val repository = OwnerRepository(application)
    val responseAction = MutableLiveData<Pair<Int, Int>>()

    fun toggleStatus(
        staffID: Int,
        ownerID: Int
    ){
        viewModelScope.launch {
            val response = repository.toggleStatus(
                staffID, ownerID
            )
            responseAction.postValue(
                Pair(response, TOGGLE_STATUS)
            )
        }
    }

    fun removeStaff(
        staffID: Int,
        ownerID: Int
    ){
        viewModelScope.launch {
            val response = repository.removeStaff(
                staffID, ownerID
            )
            responseAction.postValue(
                Pair(response, REMOVE_STAFF)
            )
        }
    }
}