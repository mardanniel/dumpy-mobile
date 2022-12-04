package com.envizioners.dumpy.ownerclient.viewmodels.shared

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.envizioners.dumpy.ownerclient.repository.SharedRepository
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class ProfilePictureVM(application: Application): AndroidViewModel(application) {

    val repository = SharedRepository(application)
    private val _changePicResponse = MutableLiveData<Int>()
    val changePicResponse: LiveData<Int> = _changePicResponse

    fun changeOwnerPic(
        ownerID: Int,
        ownerEmail: String,
        ownerPic: Uri
    ){
        viewModelScope.launch {
            val response = repository.changeOwnerPic(
                ownerID, ownerEmail, prepareFilePart("userFile", ownerPic)
            )
            _changePicResponse.postValue(response)
        }
    }

    fun changeStaffPic(
        staffID: Int,
        staffEmail: String,
        staffPic: Uri
    ){
        viewModelScope.launch {
            val response = repository.changeStaffPic(
                staffID, staffEmail, prepareFilePart("userFile", staffPic)
            )
            _changePicResponse.postValue(response)
        }
    }

    fun changeRecyclerPic(
        recyclerID: Int,
        recyclerPic: Uri
    ){
        viewModelScope.launch {
            val response = repository.changeRecyclerPic(
                recyclerID, prepareFilePart("userFile", recyclerPic)
            )
            _changePicResponse.postValue(response)
        }
    }

    private fun prepareFilePart(partName: String, createTradeItemImageUri: Uri): MultipartBody.Part {
        val file = File(createTradeItemImageUri.path)
        val requestFile: RequestBody = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(partName, file.name, requestFile)
    }
}