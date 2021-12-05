package com.beeswork.balance.ui.mainactivity

import android.Manifest
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.beeswork.balance.R
import com.beeswork.balance.data.network.service.stomp.WebSocketEvent
import com.beeswork.balance.databinding.ActivityMainBinding
import com.beeswork.balance.internal.constant.*
import com.beeswork.balance.internal.util.safeLet
import com.beeswork.balance.ui.common.BaseActivity
import com.beeswork.balance.ui.dialog.ErrorDialog
import com.google.android.gms.location.*
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.beeswork.balance.internal.util.hideKeyboard
import com.beeswork.balance.ui.mainviewpager.MainViewPagerFragment
import com.google.firebase.messaging.FirebaseMessaging
import java.util.*


class MainActivity : BaseActivity(), KodeinAware, ErrorDialog.OnRetryListener {

    override val kodein by closestKodein()
    private val fusedLocationProviderClient: FusedLocationProviderClient by instance()
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private val viewModelFactory: MainViewModelFactory by instance()
    private var onScreen = true

    private val requestLocationPermission = registerForActivityResult(RequestPermission()) { granted ->
        if (granted) bindLocationManager()
        else viewModel.saveLocationPermissionResult(false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        setupLocationManager()
        bindUI()
    }

    private fun setupLocationManager() {
        if (hasLocationPermission()) bindLocationManager()
        else requestLocationPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun bindLocationManager() {
        LocationLifecycleObserver(this, fusedLocationProviderClient, locationCallback, this)
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            locationResult?.let { _locationResult ->
                val location = _locationResult.lastLocation
                viewModel.saveLocation(location.latitude, location.longitude)
            }
        }
    }

    private fun bindUI() = lifecycleScope.launch {
        setupWebSocketEventObserver()
    }

    private suspend fun setupWebSocketEventObserver() {
        viewModel.webSocketEventLiveData.await().observeForever { webSocketEvent ->
            when {
                webSocketEvent.isError() && validateLogin(webSocketEvent) -> {
                    // TODO: show error message saying reconnect in a few seconds automatically

                }
            }
        }
    }

    private fun showWebSocketError(error: String?, errorMessage: String?) {
//        val errorTitle = getString(R.string.error_title_web_socket_disconnected)
//        ErrorDialog(error, errorTitle, errorMessage, RequestCode.CONNECT_TO_WEB_SOCKET, this, null).show(
//            supportFragmentManager,
//            ErrorDialog.TAG
//        )
    }

    override fun onRetry(requestCode: Int?) {
        requestCode?.let {
            when (it) {
//                RequestCode.CONNECT_TO_WEB_SOCKET -> viewModel.connectStomp()
            }
        }
    }

    override fun onResume() {
        super.onResume()
//        if (hasLocationPermission()) bindLocationManager()
//        else viewModel.saveLocationPermissionResult(false)
        onScreen = true
        println("onresume viewModel.connectStomp()")
        viewModel.connectStomp()
    }

    override fun onPause() {
        super.onPause()
        onScreen = false
        viewModel.disconnectStomp()
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


    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
//        this.hideKeyboard(ev)
        return super.dispatchTouchEvent(ev)
    }


}



