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

abstract class BaseLocationActivity(
    private val syncLocation: Boolean
) : BaseActivity(), KodeinAware {

    override val kodein by closestKodein()
    private val fusedLocationProviderClient: FusedLocationProviderClient by instance()
    private lateinit var viewModel: BaseLocationViewModel
    private var locationPermissionListener: LocationPermissionListener? = null


    fun onCreate(viewModel: BaseLocationViewModel, locationPermissionListener: LocationPermissionListener) {
        this.viewModel = viewModel
        this.locationPermissionListener = locationPermissionListener
    }

    private val requestLocationPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        onLocationPermissionChanged(granted)
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            locationResult?.let { _locationResult ->
                val location = _locationResult.lastLocation
                viewModel.saveLocation(location.latitude, location.longitude, syncLocation)
            }
        }
    }

    protected fun setupLocationManager() {
        if (hasLocationPermission()) {
            onLocationPermissionChanged(true)
        } else {
            requestLocationPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun onLocationPermissionChanged(granted: Boolean) {
        if (granted) {
            LocationLifecycleObserver(this, fusedLocationProviderClient, locationCallback, this)
        }
        locationPermissionListener?.onLocationPermissionChanged(granted)
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    protected fun checkLocationPermission() {
        onLocationPermissionChanged(hasLocationPermission())
    }
}