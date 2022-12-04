package com.envizioners.dumpy.ownerclient.viewmodels.recycler

import android.app.Application
import android.net.Uri
import androidx.lifecycle.*
import com.envizioners.dumpy.ownerclient.repository.RecyclerRepository
import com.envizioners.dumpy.ownerclient.response.tradeproposal.CreateTradeItem
import com.envizioners.dumpy.ownerclient.response.upload.InsertTradeResponse
import com.envizioners.dumpy.ownerclient.response.validation.FormValidationResponse
import com.google.gson.Gson
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File


class CreateTradeVM(application: Application): AndroidViewModel(application) {

    private val repository = RecyclerRepository(application)
    private val _recyclerUploadLD = MutableLiveData<FormValidationResponse>()
    val recyclerUploadLD: LiveData<FormValidationResponse> = _recyclerUploadLD

    fun insertTrade(createTradeItems: List<CreateTradeItem>, junkshopID: Int, userEmail: String, userID: Int){

        var tradeItemImages = mutableListOf<MultipartBody.Part>()
        var tradeItems = mutableListOf<String>()
        var tradeItemQuantities = mutableListOf<String>()
        var tradeMeasurements = mutableListOf<String>()

        for (i in createTradeItems.indices) {
            tradeItemImages.add(prepareFilePart("tradeitemimage[$i]", createTradeItems[i].itemImage))
            tradeItems.add(createTradeItems[i].itemName)
            tradeItemQuantities.add(createTradeItems[i].itemQuantity)
            tradeMeasurements.add(createTradeItems[i].itemMeasurement)
        }

        var gson = Gson()

        viewModelScope.launch {
            val response = repository.insertTrade(
                tradeItemImages,
                gson.toJson(tradeItems),
                gson.toJson(tradeItemQuantities),
                gson.toJson(tradeMeasurements),
                junkshopID,
                userEmail,
                userID
            )
            _recyclerUploadLD.postValue(response!!)
        }
    }

    private fun prepareFilePart(partName: String, createTradeItemImageUri: Uri): MultipartBody.Part {
        val file = File(createTradeItemImageUri.path)
        val requestFile: RequestBody = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(partName, file.name, requestFile)
    }
}