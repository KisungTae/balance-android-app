package com.beeswork.balance.ui.mainactivity

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.NavHostFragment
import com.beeswork.balance.R
import com.beeswork.balance.data.database.repository.BalanceRepository
import com.beeswork.balance.databinding.ActivityMainBinding
import com.beeswork.balance.internal.constant.*
import com.beeswork.balance.internal.provider.preference.PreferenceProvider
import com.beeswork.balance.ui.dialog.ClickedDialog
import com.beeswork.balance.ui.dialog.NewMatchDialog
import com.beeswork.balance.ui.mainviewpager.MainViewPagerFragment
import com.google.android.gms.location.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance


class MainActivity : AppCompatActivity(), KodeinAware {

    override val kodein by closestKodein()
    private val fusedLocationProviderClient: FusedLocationProviderClient by instance()
    private lateinit var broadcastReceiver: BroadcastReceiver
    private val preferenceProvider: PreferenceProvider by instance()
    private val balanceRepository: BalanceRepository by instance()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupBroadcastReceiver()
        setupLocationManager()
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

//    private fun setupBackStackListener() {
//        supportFragmentManager.addOnBackStackChangedListener {
//            val currentFragment = supportFragmentManager.fragments.last()
//            if (currentFragment is NavHostFragment) {
//                val currentFragmentInNav = currentFragment.childFragmentManager.fragments.first()
//                if (currentFragmentInNav is MainViewPagerFragment) currentFragmentInNav.onFragmentDisplayed()
//            }
//        }
//    }

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
//            NotificationType.MATCH -> NewMatchDialog("", photoKey).show(
//                supportFragmentManager,
//                NewMatchDialog.TAG
//            )
            NotificationType.CLICKED -> ClickedDialog(photoKey).show(
                supportFragmentManager,
                ClickedDialog.TAG
            )
        }
    }

    private fun setupLocationManager() {
        if (hasLocationPermission()) bindLocationManager()
        else requestLocationPermission()
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
//            if (location != null) balanceRepository.saveLocation(location.latitude, location.longitude)
        }
    }

    private fun bindLocationManager() {
        LocationLifecycleObserver(this, fusedLocationProviderClient, locationCallback, this)
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            RequestCode.ACCESS_FINE_LOCATION
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == RequestCode.ACCESS_FINE_LOCATION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                bindLocationManager()
        }
    }


//    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
//        val v: View? = currentFocus
//        if (v != null && (ev.action == MotionEvent.ACTION_UP || ev.action == MotionEvent.ACTION_MOVE) && v is EditText &&
//            !v.javaClass.name.startsWith("android.webkit.")
//        ) {
//            val sourceCoordinates = IntArray(2)
//            v.getLocationOnScreen(sourceCoordinates)
//            val x: Float = ev.rawX + v.getLeft() - sourceCoordinates[0]
//            val y: Float = ev.rawY + v.getTop() - sourceCoordinates[1]
//            if (x < v.getLeft() || x > v.getRight() || y < v.getTop() || y > v.getBottom()) {
//                hideKeyboard(this)
//            }
//        }
//        return super.dispatchTouchEvent(ev)
//    }
//
//    private fun hideKeyboard(activity: Activity?) {
//        safeLet(activity, activity?.window) { a, w ->
//            (a.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
//                a.window.decorView.windowToken,
//                0
//            )
//        }
//    }

    fun hideKeyboard(view: View?) {
        if (view != null) {
            val imm: InputMethodManager = view.context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            if (imm != null) imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

}



