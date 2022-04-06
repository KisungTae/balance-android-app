package com.beeswork.balance.ui.registeractivity.location

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.beeswork.balance.databinding.FragmentLocationBinding
import com.beeswork.balance.ui.common.LocationPermissionListener
import com.beeswork.balance.ui.mainactivity.MainActivity
import com.beeswork.balance.ui.registeractivity.BaseRegisterStepFragment
import com.beeswork.balance.ui.registeractivity.RegisterActivity
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class LocationFragment : BaseRegisterStepFragment(), LocationPermissionListener {

    private lateinit var binding: FragmentLocationBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentLocationBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requestLocationPermission()
    }

    private fun requestLocationPermission() {
        getRegisterActivity()?.requestLocationPermission()
    }

    private fun getRegisterActivity(): RegisterActivity? {
        activity?.let { _activity ->
            return if (_activity is RegisterActivity) {
                _activity
            } else {
                null
            }
        } ?: return null
    }

    override fun onResume() {
        super.onResume()
        getRegisterActivity()?.checkLocationPermission()
    }

    override fun onLocationPermissionChanged(granted: Boolean) {
        if (granted) {
            moveToNextTab()
        } else {
            binding.llRegisterLocationLoadingWrapper.visibility = View.GONE
            binding.llRegisterLocationErrorWrapper.visibility = View.VISIBLE
        }
    }
}