package com.beeswork.balance.ui.common

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.beeswork.balance.ui.mainactivity.LocationLifecycleObserver
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance

abstract class BaseLocationActivity: BaseActivity(), KodeinAware {

    override val kodein by closestKodein()
    private val fusedLocationProviderClient: FusedLocationProviderClient by instance()
    protected var locationViewModel: BaseLocationViewModel? = null
    protected val locationPermissionListeners = mutableListOf<LocationPermissionListener>()

    private val requestLocationPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        onLocationPermissionChanged(granted)
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            locationResult?.let { _locationResult ->
                val location = _locationResult.lastLocation
                locationViewModel?.saveLocation(location.latitude, location.longitude)
            }
        }
    }

    protected fun setupLocationManager() {
        if (hasLocationPermission()) {
            bindLocationManager()
        } else {
            requestLocationPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun onLocationPermissionChanged(granted: Boolean) {
        for (locationPermissionListener in locationPermissionListeners) {
            locationPermissionListener.onLocationPermissionChanged(granted)
        }
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private fun bindLocationManager() {
        onLocationPermissionChanged(true)
        LocationLifecycleObserver(this, fusedLocationProviderClient, locationCallback, this)
    }

    protected fun doCheckLocationPermission() {
        if (hasLocationPermission()) {
            bindLocationManager()
        } else {
            onLocationPermissionChanged(false)
        }
    }
}