package com.envizioners.dumpy.ownerclient.viewmodels.recycler

import android.app.Application
import androidx.lifecycle.*
import com.envizioners.dumpy.ownerclient.repository.RecyclerRepository
import com.envizioners.dumpy.ownerclient.response.authrecycler.AuthRecyclerLogin
import com.envizioners.dumpy.ownerclient.response.authrecycler.AuthRecyclerRegister
import com.envizioners.dumpy.ownerclient.response.shared.LoginFields
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.launch

class RecyclerAuthVM(application: Application) : AndroidViewModel(application) {

    private val repository = RecyclerRepository(application)
    private val _recyclerLoginLD = MutableLiveData<AuthRecyclerLogin>()
    val recyclerLoginLD: LiveData<AuthRecyclerLogin> = _recyclerLoginLD
    val recyclerRegisterMLD = MutableLiveData<AuthRecyclerRegister>()

    fun loginInput(
        userEmail: String,
        userPassword: String
    ){
        viewModelScope.launch {
            if(validateEmail(userEmail) && userPassword.isNotBlank()){
                val response = repository.authenticateRecycler(
                    userEmail,
                    userPassword
                )
                _recyclerLoginLD.postValue(response!!)
            }else if(!validateEmail(userEmail)){
                val response = AuthRecyclerLogin(
                    false,
                    false,
                    null,
                    LoginFields(
                        "Please enter a valid email address.",
                        "",
                        ""
                    )
                )
                _recyclerLoginLD.postValue(response)
            }
        }
    }

    fun register(
        email: String,
        fname: String,
        mname: String,
        lname: String,
        contact: String,
        birthdate: String,
        street: String,
        barangay: String,
        city: String,
        password: String,
        confPass: String,
        agree: String
    ){
        viewModelScope.launch {
            val response = repository.registerRecycler(
                email,
                fname,
                mname,
                lname,
                contact,
                birthdate,
                street,
                barangay,
                city,
                password,
                confPass,
                agree
            )
            recyclerRegisterMLD.postValue(response)
        }
    }

    private fun validateEmail(field: String): Boolean = android.util.Patterns.EMAIL_ADDRESS.matcher(field).matches()
}