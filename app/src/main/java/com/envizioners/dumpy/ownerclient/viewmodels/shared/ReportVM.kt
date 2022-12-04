package com.envizioners.dumpy.ownerclient.viewmodels.shared

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.envizioners.dumpy.ownerclient.repository.SharedRepository
import com.envizioners.dumpy.ownerclient.response.validation.FormValidationResponse
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class ReportVM(application: Application): AndroidViewModel(application) {

    val repository = SharedRepository(application)
    val reportMLD = MutableLiveData<FormValidationResponse>()

    fun insertTrade(
        source_id: Int,
        source_role: String,
        report_text: String,
        report_type: String,
        report_image: Uri?
    ){
        viewModelScope.launch {
            val reportImageMultipartBody = if (report_image != null){
                prepareFilePart("report_img", report_image)
            }else{
                null
            }

            val response = repository.insertReport(
                source_id,
                source_role,
                report_text,
                report_type,
                reportImageMultipartBody
            )
            reportMLD.postValue(response!!)
        }
    }

    private fun prepareFilePart(partName: String, createTradeItemImageUri: Uri): MultipartBody.Part {
        val file = File(createTradeItemImageUri.path)
        val requestFile: RequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
        return MultipartBody.Part.createFormData(partName, file.name, requestFile)
    }

}