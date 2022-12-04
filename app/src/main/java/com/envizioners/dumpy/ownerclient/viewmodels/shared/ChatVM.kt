package com.envizioners.dumpy.ownerclient.viewmodels.shared

import android.app.Application
import androidx.lifecycle.*
import com.envizioners.dumpy.ownerclient.repository.SharedRepository
import kotlinx.coroutines.launch

class ChatVM(application: Application): AndroidViewModel(application) {

    private val repository = SharedRepository(application)
    private val _chatMenuMLD = MutableLiveData<List<Any>>()
    val chatMenuLD: LiveData<List<Any>> = _chatMenuMLD
    val chatToken = MutableLiveData<String>()

    fun getConversationList(ID: Int, type: String){
        viewModelScope.launch {
            if(type == "DUMPY_RECYCLER"){
                val response = repository.getRecyclerConversationList(ID)
                _chatMenuMLD.postValue(response?.result)
            }else{
                val response = repository.getJunkshopConversationList(ID)
                _chatMenuMLD.postValue(response?.result)
            }
        }
    }

    fun getConversationToken(
        chat_sender: Int,
        chat_receiver: Int
    ){
        viewModelScope.launch {
            val response = repository.getConversationToken(
                chat_sender,
                chat_receiver
            )
            chatToken.postValue(response!!)
        }
    }
}