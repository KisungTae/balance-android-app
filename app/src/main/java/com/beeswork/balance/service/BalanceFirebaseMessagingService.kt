package com.beeswork.balance.service

import com.beeswork.balance.data.repository.BalanceRepository
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService
import com.google.firebase.messaging.FirebaseMessagingService
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance


class BalanceFirebaseMessagingService: FirebaseMessagingService(), KodeinAware {

    override val kodein by closestKodein()
    private val balanceRepository: BalanceRepository by instance()

    override fun onNewToken(token: String) {
        balanceRepository.insertFirebaseMessagingToken(token)
    }
}