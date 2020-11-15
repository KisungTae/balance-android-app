package com.beeswork.balance.ui.main

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import com.beeswork.balance.R
import com.beeswork.balance.data.database.repository.BalanceRepository
import com.beeswork.balance.internal.*
import com.beeswork.balance.internal.constant.*
import com.beeswork.balance.internal.provider.PreferenceProvider
import com.beeswork.balance.ui.dialog.ClickedDialog
import com.beeswork.balance.ui.dialog.MatchDialog
import com.google.android.gms.location.*
import com.google.android.material.bottomnavigation.LabelVisibilityMode
import kotlinx.android.synthetic.main.activity_main.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance

class MainActivity : AppCompatActivity(), KodeinAware {

    override val kodein by closestKodein()
    private val fusedLocationProviderClient: FusedLocationProviderClient by instance()
    private lateinit var broadcastReceiver: BroadcastReceiver
    private val preferenceProvider: PreferenceProvider by instance()
    private val balanceRepository: BalanceRepository by instance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fcvHost) as NavHostFragment

        nvBottom.labelVisibilityMode = LabelVisibilityMode.LABEL_VISIBILITY_UNLABELED
        nvBottom.setupWithNavController(navHostFragment.navController)

        setupBroadcastReceiver()

        if (hasLocationPermission()) bindLocationManager()
        else requestLocationPermission()
    }

    override fun onResume() {
        super.onResume()

        val type = intent.getStringExtra(FCMDataKey.NOTIFICATION_TYPE)
        if (type != null) onReceiveFCMNotification(intent)

        val filter = IntentFilter(IntentAction.RECEIVED_FCM_NOTIFICATION)
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, filter)
    }

    override fun onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver)
        super.onPause()
    }

    private fun setupBroadcastReceiver() {
        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.action) {
                    IntentAction.RECEIVED_FCM_NOTIFICATION -> onReceiveFCMNotification(intent)
                }
            }
        }
    }

    private fun onReceiveFCMNotification(intent: Intent?) {

        val notificationType = intent!!.getStringExtra(FCMDataKey.NOTIFICATION_TYPE)
        val photoKey = intent.getStringExtra(FCMDataKey.PHOTO_KEY)

        when (notificationType) {
            NotificationType.MATCH -> MatchDialog("", photoKey).show(
                supportFragmentManager,
                DialogTag.MATCH_DIALOG
            )
            NotificationType.CLICKED -> ClickedDialog(photoKey).show(
                supportFragmentManager,
                DialogTag.CLICKED_DIALOG
            )
        }
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private val locationCallback = object : LocationCallback() {

        override fun onLocationResult(locationResult: LocationResult?) {
            super.onLocationResult(locationResult)
            val location = locationResult?.lastLocation
            if (location != null)
                balanceRepository.saveLocation(location.latitude, location.longitude)
        }
    }

    private fun bindLocationManager() {
        LocationLifecycleObserver(this, fusedLocationProviderClient, locationCallback, this)
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            PermissionRequestCode.ACCESS_FINE_LOCATION
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PermissionRequestCode.ACCESS_FINE_LOCATION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                bindLocationManager()
        }
    }

}