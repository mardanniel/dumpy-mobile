package com.envizioners.dumpy.ownerclient.viewmodels.shared

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.envizioners.dumpy.ownerclient.notification.PushNotification
import com.envizioners.dumpy.ownerclient.repository.OwnerRepository
import kotlinx.coroutines.launch

class NotificationVM(application: Application): AndroidViewModel(application) {

    private val repository = OwnerRepository(application)

    fun sendNotification(notification: PushNotification, activity: String) {
        viewModelScope.launch {
            repository.sendNotification(notification, activity)
        }
    }
}