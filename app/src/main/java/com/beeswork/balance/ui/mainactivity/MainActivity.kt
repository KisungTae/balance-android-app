package com.beeswork.balance.ui.mainactivity

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.beeswork.balance.databinding.ActivityMainBinding
import kotlinx.coroutines.launch
import org.kodein.di.generic.instance
import com.beeswork.balance.R
import com.beeswork.balance.internal.util.MessageSource
import com.beeswork.balance.internal.util.Navigator
import com.beeswork.balance.ui.common.BaseLocationActivity
import com.beeswork.balance.ui.common.LocationPermissionListener
import com.beeswork.balance.ui.mainviewpagerfragment.MainViewPagerFragment


class MainActivity : BaseLocationActivity(), LocationPermissionListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private val viewModelFactory: MainViewModelFactory by instance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        super.locationViewModel = viewModel
        super.locationPermissionListeners.add(this)
        bindUI()
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

    fun requestLocationPermission() {
        setupLocationManager()
    }

    fun checkLocationPermission() {
        doCheckLocationPermission()
    }

    override fun onResume() {
        super.onResume()
//        viewModel.connectStomp()
    }

    override fun onPause() {
        super.onPause()
//        viewModel.disconnectStomp()
    }

    override fun onLocationPermissionChanged(granted: Boolean) {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fcvMain)
        navHostFragment?.childFragmentManager?.fragments?.forEach { fragment ->
            if (fragment is MainViewPagerFragment) {
                fragment.onLocationPermissionChanged(granted)
            }
        }
    }


}



