package com.beeswork.balance.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.beeswork.balance.R
import com.beeswork.balance.data.repository.BalanceRepository
import com.beeswork.balance.internal.constant.IntentAction
import com.beeswork.balance.internal.constant.IntentDataKey
import com.beeswork.balance.internal.constant.NotificationChannelConstant
import com.beeswork.balance.ui.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance


class BalanceFirebaseMessagingService : FirebaseMessagingService(), KodeinAware {

    override val kodein by closestKodein()
    private val balanceRepository: BalanceRepository by instance()

    override fun onNewToken(token: String) {
        balanceRepository.insertFirebaseMessagingToken(token)
    }



    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val messageReceived = remoteMessage.data
        println("message received: $messageReceived")

        createNotificationChannel()

//        val intent = Intent().apply {
//            action = IntentAction.SEND_MESSAGE
//            putExtra(IntentDataKey.MESSAGE, "this is test message from fireabseservice")
//        }
//        LocalBroadcastManager.getInstance(this)

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        var builder = NotificationCompat.Builder(this, NotificationChannelConstant.ID)
            .setSmallIcon(R.drawable.ic_baseline_account_circle)
            .setContentTitle("this is notification title")
            .setContentText("this is notification message")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(this)) {
            // notificationId is a unique int for each notification that you must define
            notify(3, builder.build())
        }

    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = NotificationChannelConstant.NAME
            val descriptionText = NotificationChannelConstant.DESCRIPTION
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel =
                NotificationChannel(NotificationChannelConstant.ID, name, importance).apply {
                    description = descriptionText
                }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}