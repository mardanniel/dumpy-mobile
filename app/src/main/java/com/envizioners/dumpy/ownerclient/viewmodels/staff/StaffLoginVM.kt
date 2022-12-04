package com.envizioners.dumpy.ownerclient.viewmodels.staff

import android.app.Application
import androidx.lifecycle.*
import com.envizioners.dumpy.ownerclient.repository.StaffRepository
import com.envizioners.dumpy.ownerclient.response.authstaff.AuthStaffLogin
import com.envizioners.dumpy.ownerclient.response.shared.LoginFields
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.launch

class StaffLoginVM(application: Application): AndroidViewModel(application) {

    private val repository = StaffRepository(application)
    private val _staffLoginLD = MutableLiveData<AuthStaffLogin>()
    val staffLoginLD: LiveData<AuthStaffLogin> = _staffLoginLD

    fun loginInput(
        userEmail: String,
        userPassword: String
    ){
        viewModelScope.launch {
            if(validateEmail(userEmail) && userPassword.isNotBlank()){
                val response = repository.authenticateStaff(
                    userEmail,
                    userPassword
                )
                _staffLoginLD.postValue(response!!)
            }else if(!validateEmail(userEmail)){
                val response = AuthStaffLogin(
                    false,
                    false,
                    null,
                    LoginFields(
                        "Please enter a valid email address.",
                        "",
                        ""
                    )
                )
                _staffLoginLD.postValue(response)
            }
        }
    }

    private fun validateEmail(field: String): Boolean = android.util.Patterns.EMAIL_ADDRESS.matcher(field).matches()
}