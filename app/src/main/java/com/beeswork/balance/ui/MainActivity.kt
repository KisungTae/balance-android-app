package com.beeswork.balance.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import com.beeswork.balance.R
import com.beeswork.balance.internal.*
import com.beeswork.balance.internal.constant.PreferencesDefault
import com.beeswork.balance.internal.constant.PreferencesKey
import com.beeswork.balance.internal.constant.PermissionRequestCode
import com.google.android.gms.location.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.android.material.bottomnavigation.LabelVisibilityMode
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.InstanceIdResult
import kotlinx.android.synthetic.main.activity_main.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance

class MainActivity : AppCompatActivity(), KodeinAware {

    override val kodein by closestKodein()
    private val fusedLocationProviderClient: FusedLocationProviderClient by instance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fcvHost) as NavHostFragment

        nvBottom.labelVisibilityMode = LabelVisibilityMode.LABEL_VISIBILITY_UNLABELED
        nvBottom.setupWithNavController(navHostFragment.navController)

        if (hasLocationPermission()) bindLocationManager()
        else requestLocationPermission()

//        FirebaseInstanceId.getInstance().instanceId
//            .addOnCompleteListener(OnCompleteListener { task ->
//                if (!task.isSuccessful) {
//                    return@OnCompleteListener
//                }
//
//                // Get new Instance ID token
//                val token = task.result?.token
//
//                // Log and toast
////                val msg = getString(R.string.msg_token_fmt, token)
////                Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
//
//                val bac = 123
//            })
        // [END retrieve_current_token]

    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(this,
                                                 Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private val locationCallback = object : LocationCallback() {

        override fun onLocationResult(locationResult: LocationResult?) {
            super.onLocationResult(locationResult)
            val location = locationResult?.lastLocation
            if (location != null)
                updateLocation(location.latitude, location.longitude)
        }
    }

    private fun bindLocationManager() {
        println("bindLocationManager")
        LocationLifecycleObserver(this, fusedLocationProviderClient, locationCallback, this)
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(this,
                                          arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                                          PermissionRequestCode.ACCESS_FINE_LOCATION)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PermissionRequestCode.ACCESS_FINE_LOCATION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) bindLocationManager()
            else {
                updateLocation(PreferencesDefault.LATITUDE, PreferencesDefault.LONGITUDE)
                Toast.makeText(this, "manually configure location", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun updateLocation(lat: Double, lon: Double) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val editor = preferences.edit()
        editor.putDouble(PreferencesKey.LATITUDE, lat)
        editor.putDouble(PreferencesKey.LONGITUDE, lon)
        editor.apply()
    }

}