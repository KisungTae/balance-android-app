package com.beeswork.balance.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.beeswork.balance.internal.constant.LOCATION_REQUEST_FASTEST_INTERVAL
import com.beeswork.balance.internal.constant.LOCATION_REQUEST_INTERVAL
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest

class LocationLifecycleObserver(
    lifecycleOwner: LifecycleOwner,
    private val fusedLocationProviderClient: FusedLocationProviderClient,
    private val locationCallback: LocationCallback,
    private val context: Context
) : LifecycleObserver {

    init {
        lifecycleOwner.lifecycle.addObserver(this)
    }

    private val locationRequest = LocationRequest().apply {
        interval = LOCATION_REQUEST_INTERVAL
        fastestInterval = LOCATION_REQUEST_FASTEST_INTERVAL
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(context,
                                              Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun removeLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }
}