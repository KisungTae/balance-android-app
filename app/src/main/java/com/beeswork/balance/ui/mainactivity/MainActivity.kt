package com.beeswork.balance.ui.mainactivity

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MotionEvent
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.beeswork.balance.R
import com.beeswork.balance.databinding.ActivityMainBinding
import com.beeswork.balance.internal.constant.*
import com.beeswork.balance.ui.common.BaseActivity
import com.beeswork.balance.ui.dialog.ErrorDialog
import com.google.android.gms.location.*
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import com.beeswork.balance.internal.exception.NoInternetConnectivityException
import com.beeswork.balance.internal.util.MessageSource
import com.beeswork.balance.internal.util.Navigator


class MainActivity : BaseActivity(), KodeinAware, ErrorDialog.RetryListener {

    override val kodein by closestKodein()
    private val fusedLocationProviderClient: FusedLocationProviderClient by instance()
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private val viewModelFactory: MainViewModelFactory by instance()

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
            if (webSocketEvent.isError()) {
                if (ExceptionCode.isLoginException(webSocketEvent.exception)) {
                    val message = MessageSource.getMessage(this, webSocketEvent.exception)
                    Navigator.finishToLoginActivity(this, message)
                } else if (webSocketEvent.exception is NoInternetConnectivityException) {
                    val title = getString(R.string.error_title_web_socket_disconnected)
                    val message = MessageSource.getMessage(this, webSocketEvent.exception)
                    ErrorDialog.show(title, message, supportFragmentManager)
                }
            }
        }
    }

    override fun onRetry(requestCode: Int?) {
        when (requestCode) {
            RequestCode.CONNECT_TO_WEB_SOCKET -> viewModel.connectStomp()
        }
    }

    override fun onResume() {
        super.onResume()
//        if (hasLocationPermission()) bindLocationManager()
//        else viewModel.saveLocationPermissionResult(false)
        viewModel.connectStomp()
    }

    override fun onPause() {
        super.onPause()
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

    companion object {

    }


}



