package com.envizioners.dumpy.ownerclient.viewmodels.recycler

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.envizioners.dumpy.ownerclient.repository.RecyclerRepository
import com.envizioners.dumpy.ownerclient.response.recycler_main.RecyclerProfile
import kotlinx.coroutines.launch

class RecyclerProfileVM(application: Application): AndroidViewModel(application) {

    val repository = RecyclerRepository(application)
    private val _recyclerInfo = MutableLiveData<RecyclerProfile>()
    val recyclerInfo: LiveData<RecyclerProfile> = _recyclerInfo

    fun getUserInfo(userID: Int){
        viewModelScope.launch {
            val response = repository.getInfo(
                userID
            )
            _recyclerInfo.postValue(response)
        }
    }

    fun changeProfilePicture(userID: Int){
        TODO()
    }
}