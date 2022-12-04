package com.envizioners.dumpy.ownerclient.viewmodels.recycler

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.envizioners.dumpy.ownerclient.repository.RecyclerRepository
import com.envizioners.dumpy.ownerclient.response.points.CouponDetail
import com.envizioners.dumpy.ownerclient.response.points.ExchangePointsResult
import com.envizioners.dumpy.ownerclient.response.points.PointsDetail
import kotlinx.coroutines.launch

class ExchangePointsVM(application: Application): AndroidViewModel(application) {

    val repository = RecyclerRepository(application)
    var exchangePointsMLD = MutableLiveData<ExchangePointsResult>()
    var couponsMLD = MutableLiveData<List<CouponDetail>>()
    var sendRequestResponse = MutableLiveData<Int>()

    fun getExchangePointsList(userID: Int){
        viewModelScope.launch {
            val response = repository.getExchangePointsList(userID)
            if(response?.points_details != null){
                exchangePointsMLD.postValue(response!!)
            }else{
                exchangePointsMLD.postValue(
                    ExchangePointsResult(
                    "0",
                    "",
                    mutableListOf(),
                        false
                    )
                )
            }
        }
    }

    fun getCouponsList(userID: Int){
        viewModelScope.launch {
            val response = repository.getCouponsList(userID)
            if(response?.coupon_details != null){
                couponsMLD.postValue(response?.coupon_details)
            }else{
                couponsMLD.postValue(emptyList())
            }
        }
    }

    fun sendRequest(
        userID: Int,
        establishmentID: Int
    ) {
        viewModelScope.launch {
            val response = repository.sendRequest(
                userID,
                establishmentID
            )
            sendRequestResponse.postValue(response!!)
        }
    }

}