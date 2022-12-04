package com.envizioners.dumpy.ownerclient.viewmodels.owner

import android.app.Application
import androidx.lifecycle.*
import com.envizioners.dumpy.ownerclient.repository.OwnerRepository
import com.envizioners.dumpy.ownerclient.response.staff_main.StaffResultBody
import kotlinx.coroutines.launch

class ManageStaffVM(application: Application): AndroidViewModel(application) {

    private val repository = OwnerRepository(application)
    private val _staffListMLD = MutableLiveData<List<StaffResultBody>>()
    val staffListLD: LiveData<List<StaffResultBody>> = _staffListMLD

    fun getStaffList(junkshopOwnerID: Int){
        viewModelScope.launch {
            val response = repository.getStaffList(junkshopOwnerID)
            _staffListMLD.postValue(response?.result)
        }
    }
}