package com.envizioners.dumpy.ownerclient.viewmodels.owner

import android.app.Application
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import com.envizioners.dumpy.ownerclient.repository.OwnerRepository
import com.envizioners.dumpy.ownerclient.response.authowner.AuthOwnerLogin
import com.envizioners.dumpy.ownerclient.response.authowner.AuthOwnerRegister
import com.envizioners.dumpy.ownerclient.response.shared.LoginFields
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class OwnerAuthVM(application: Application): AndroidViewModel(application) {

    private val repository = OwnerRepository(application)
    private val _ownerLoginLD = MutableLiveData<AuthOwnerLogin>()
    val ownerLoginLD: LiveData<AuthOwnerLogin> = _ownerLoginLD
    val ownerRegisterMLD = MutableLiveData<AuthOwnerRegister>()

    fun loginInput(
        userEmail: String,
        userPassword: String
    ){
        viewModelScope.launch {
            if(validateEmail(userEmail) && userPassword.isNotBlank()){
                val response = repository.authenticateOwner(userEmail, userPassword)
                _ownerLoginLD.postValue(response!!)
            }else if(!validateEmail(userEmail)){
                val response = AuthOwnerLogin(
                    false,
                    false,
                    LoginFields(
                        "Please enter a valid email address.",
                        "",
                        ""),
                    null)
                _ownerLoginLD.postValue(response)
            }
        }
    }

    fun registerOwner(
        email: String,
        fname: String,
        mname: String,
        lname: String,
        contact: String,
        birthdate: String,
        gender: String,
        street: String,
        barangay: String,
        city: String,
        password: String,
        confPass: String,
        agree: String,
        establishmentName: String,
        establishmentStreet: String,
        establishmentBarangay: String,
        establishmentCity: String,
        establishmentPhoto: Uri,
        businessPermit: Uri,
        sanitaryPermit: Uri,
        mayorResidence: Uri
    ){
        viewModelScope.launch {
            val response = repository.registerOwner(
                email,
                fname,
                mname,
                lname,
                contact,
                birthdate,
                gender,
                street,
                barangay,
                city,
                password,
                confPass,
                agree,
                establishmentName,
                establishmentStreet,
                establishmentBarangay,
                establishmentCity,
                prepareFilePart("junkshop_photo", establishmentPhoto),
                prepareFilePart("business_permit", businessPermit),
                prepareFilePart("sanitary_permit", sanitaryPermit),
                prepareFilePart("mayor_residence", mayorResidence)
            )
            ownerRegisterMLD.postValue(response)
        }
    }

    private fun prepareFilePart(partName: String, createTradeItemImageUri: Uri): MultipartBody.Part {
        val file = File(createTradeItemImageUri.path)
        val requestFile: RequestBody = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(partName, file.name, requestFile)
    }

    private fun validateEmail(field: String): Boolean = android.util.Patterns.EMAIL_ADDRESS.matcher(field).matches()
}

