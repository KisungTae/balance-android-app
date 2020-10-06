package com.beeswork.balance.data.network

import com.google.firebase.messaging.FirebaseMessagingService

class BalanceFirebaseMessagingService: FirebaseMessagingService() {

    override fun onNewToken(token: String) {
//         super.onNewToken(token)
        println("firebas token: $token ")
    }
}