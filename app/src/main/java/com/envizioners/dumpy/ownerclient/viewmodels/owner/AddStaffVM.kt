package com.envizioners.dumpy.ownerclient.viewmodels.owner

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.envizioners.dumpy.ownerclient.repository.OwnerRepository
import com.envizioners.dumpy.ownerclient.response.owner_main.AddStaffResponse
import kotlinx.coroutines.launch

class AddStaffVM(application: Application): AndroidViewModel(application) {

    val repository = OwnerRepository(application)
    val response = MutableLiveData<AddStaffResponse>()

    fun addStaff(
        email: String,
        password: String,
        confPassword: String,
        contact: String,
        username: String,
        fname: String,
        lname: String,
        age: Int,
        ownerID: Int
    ) {
        viewModelScope.launch {
            val request = repository.addStaff(
                email,
                password,
                confPassword,
                contact,
                username,
                fname,
                lname,
                age,
                ownerID
            )
            response.postValue(request)
        }
    }
}