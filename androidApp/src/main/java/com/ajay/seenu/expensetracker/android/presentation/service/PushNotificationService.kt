package com.ajay.seenu.expensetracker.android.presentation.service

import com.google.firebase.messaging.FirebaseMessagingService

class PushNotificationService: FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }
}