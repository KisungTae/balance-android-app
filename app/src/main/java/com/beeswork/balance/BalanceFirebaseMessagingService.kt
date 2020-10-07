package com.beeswork.balance

import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService
import com.google.firebase.messaging.FirebaseMessagingService


class BalanceFirebaseMessagingService: FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        println("new token: $token")
    }

}