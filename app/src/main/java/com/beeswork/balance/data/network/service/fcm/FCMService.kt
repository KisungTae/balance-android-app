package com.beeswork.balance.data.network.service.fcm

import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.beeswork.balance.data.database.repository.setting.SettingRepository
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.internal.constant.IntentAction
import com.beeswork.balance.internal.util.safeLaunch
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance


class FCMService : FirebaseMessagingService(), KodeinAware {

    override val kodein by closestKodein()
    private val settingRepository: SettingRepository by instance()

    override fun onNewToken(token: String) {
        CoroutineScope(Dispatchers.IO).safeLaunch<Any>(null) {
            settingRepository.saveFCMToken(token)
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        println(remoteMessage.data)

//        val intent = Intent().apply { action = IntentAction.RECEIVED_FCM_NOTIFICATION }
//
//        for ((key, value) in remoteMessage.data) {
//            intent.putExtra(key, value)
//        }
//        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    override fun onDeletedMessages() {

        super.onDeletedMessages()
    }

//    private fun showNotification() {
//        val intent = Intent(this, MainActivity::class.java).apply {
//            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//        }
//        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
//
//        val builder = NotificationCompat.Builder(this, "CHANNEL_ID")
//            .setSmallIcon(R.drawable.ic_baseline_account_circle)
//            .setContentTitle("My notification")
//            .setContentText("Hello World!")
//            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//            // Set the intent that will fire when the user taps the notification
//            .setContentIntent(pendingIntent)
//            .setAutoCancel(true)
//
//        with(NotificationManagerCompat.from(this)) {
//            // notificationId is a unique int for each notification that you must define
//            notify(1, builder.build())
//        }
//    }
//
//
//    private fun createNotificationChannel() {
//        // Create the NotificationChannel, but only on API 26+ because
//        // the NotificationChannel class is new and not in the support library
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//
//            val importance = NotificationManager.IMPORTANCE_DEFAULT
//            val channel = NotificationChannel("CHANNEL_ID", "name", importance).apply {
//                description = "descriptionText"
//            }
//            // Register the channel with the system
//            val notificationManager: NotificationManager =
//                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//            notificationManager.createNotificationChannel(channel)
//        }
//    }
}