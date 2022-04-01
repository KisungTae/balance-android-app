package com.beeswork.balance.ui.mainactivity

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MotionEvent
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.beeswork.balance.databinding.ActivityMainBinding
import com.beeswork.balance.ui.common.BaseActivity
import com.beeswork.balance.ui.dialog.ErrorDialog
import com.google.android.gms.location.*
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import com.beeswork.balance.internal.util.MessageSource
import com.beeswork.balance.internal.util.Navigator


class MainActivity : BaseActivity(), KodeinAware, ErrorDialog.RetryListener {

    override val kodein by closestKodein()
    private val fusedLocationProviderClient: FusedLocationProviderClient by instance()
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private val viewModelFactory: MainViewModelFactory by instance()

    private val requestLocationPermission = registerForActivityResult(RequestPermission()) { granted ->
        if (granted) {
            bindLocationManager()
        } else {
            viewModel.saveLocationPermissionResult(false)
        }
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            locationResult?.let { _locationResult ->
                val location = _locationResult.lastLocation
                viewModel.saveLocation(location.latitude, location.longitude)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupLocationManager()
        bindUI()
    }

    private fun setupLocationManager() {
        if (hasLocationPermission()) {
            println("has location permission")
            bindLocationManager()
        } else {
            requestLocationPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private fun bindLocationManager() {
        LocationLifecycleObserver(this, fusedLocationProviderClient, locationCallback, this)
    }



    private fun bindUI() = lifecycleScope.launch {
        observeWebSocketEventUIStateLiveData()
    }

    private suspend fun observeWebSocketEventUIStateLiveData() {
        viewModel.webSocketEventUIStateLiveData.await().observe(this) { webSocketEventUIState ->
            if (webSocketEventUIState.shouldLogout) {
                val message = MessageSource.getMessage(this, webSocketEventUIState.exception)
                Navigator.finishToLoginActivity(this, message)
            }
        }
    }

    override fun onRetry(requestCode: Int?) {
//        when (requestCode) {
//            RequestCode.CONNECT_TO_STOMP -> viewModel.connectStomp(true)
//        }
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



