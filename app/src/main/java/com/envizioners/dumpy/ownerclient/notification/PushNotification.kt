package com.envizioners.dumpy.ownerclient.notification

data class PushNotification(
    val data: NotificationData,
    val to: String,
    val priority: String
)
