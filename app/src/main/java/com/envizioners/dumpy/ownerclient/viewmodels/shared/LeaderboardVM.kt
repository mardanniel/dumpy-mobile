package com.envizioners.dumpy.ownerclient.viewmodels.shared

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.envizioners.dumpy.ownerclient.repository.SharedRepository
import com.envizioners.dumpy.ownerclient.response.shared.LeaderboardPlacerResultBody
import kotlinx.coroutines.launch

class LeaderboardVM(application: Application): AndroidViewModel(application) {

    val repository = SharedRepository(application)
    val leaderboardMLD = MutableLiveData<List<LeaderboardPlacerResultBody>>()

    fun getLeaderboard(){
        viewModelScope.launch {
            var response = repository.getLeaderboard()
            leaderboardMLD.postValue(response?.result!!)
        }
    }
}