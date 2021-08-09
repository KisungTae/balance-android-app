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
import com.beeswork.balance.ui.mainviewpager.MainViewPagerFragment
import com.google.firebase.messaging.FirebaseMessaging


class MainActivity : BaseActivity(), KodeinAware, ErrorDialog.OnRetryListener {

    private val fusedLocationProviderClient: FusedLocationProviderClient by instance()

    override val kodein by closestKodein()
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
        setupLocationManager()
        bindUI()
        viewModel.connectStomp()
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
        observeFCMTokenActiveLiveData()
    }

    private suspend fun observeFCMTokenActiveLiveData() {
        viewModel.fcmTokenActive.await().observe(this) {
            if (onScreen) viewModel.connectStomp()
        }
    }

    private suspend fun setupWebSocketEventObserver() {
        viewModel.webSocketEventLiveData.await().observeForever {
            when (it.type) {
                WebSocketEvent.Type.ERROR -> {
                    when (it.error) {
                        ExceptionCode.ACCOUNT_NOT_FOUND_EXCEPTION,
                        ExceptionCode.ACCOUNT_BLOCKED_EXCEPTION,
                        ExceptionCode.ACCOUNT_DELETED_EXCEPTION -> moveToLoginActivity(it.error, it.errorMessage)
                        else -> showWebSocketError(it.error, it.errorMessage)
                    }
                }
                else -> println()
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
                RequestCode.CONNECT_TO_WEB_SOCKET -> viewModel.connectStomp()
            }
        }
    }

    override fun onResume() {
        super.onResume()
//        if (hasLocationPermission()) bindLocationManager()
//        else viewModel.saveLocationPermissionResult(false)
        onScreen = true
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
        val v: View? = currentFocus
        if (v != null && (ev.action == MotionEvent.ACTION_UP || ev.action == MotionEvent.ACTION_MOVE) && v is EditText &&
            !v.javaClass.name.startsWith("android.webkit.")
        ) {
            val sourceCoordinates = IntArray(2)
            v.getLocationOnScreen(sourceCoordinates)
            val x: Float = ev.rawX + v.getLeft() - sourceCoordinates[0]
            val y: Float = ev.rawY + v.getTop() - sourceCoordinates[1]
            if (x < v.getLeft() || x > v.getRight() || y < v.getTop() || y > v.getBottom()) {
                hideKeyboard(this)
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun hideKeyboard(activity: Activity?) {
        safeLet(activity, activity?.window) { a, w ->
            (a.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
                a.window.decorView.windowToken,
                0
            )
        }
    }

    fun hideKeyboard(view: View?) {
        if (view != null) {
            val imm: InputMethodManager = view.context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            if (imm != null) imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

}



