package com.beeswork.balance.ui.common

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.beeswork.balance.ui.mainactivity.LocationLifecycleObserver
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult

abstract class BaseLocationActivity: BaseActivity() {

    protected var fusedLocationProviderClient: FusedLocationProviderClient? = null
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
            onLocationPermissionChanged(true)
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
        fusedLocationProviderClient?.let { _fusedLocationProviderClient ->
            LocationLifecycleObserver(this, _fusedLocationProviderClient, locationCallback, this)
        }
    }
}