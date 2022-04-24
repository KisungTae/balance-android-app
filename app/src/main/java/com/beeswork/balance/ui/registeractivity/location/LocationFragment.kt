package com.beeswork.balance.ui.registeractivity.location

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.beeswork.balance.databinding.FragmentLocationBinding
import com.beeswork.balance.ui.common.BaseFragment
import com.beeswork.balance.ui.common.LocationPermissionListener
import com.beeswork.balance.ui.common.RegisterStepListener
import com.beeswork.balance.ui.common.LocationRequestListener

class LocationFragment(
    private val registerStepListener: RegisterStepListener,
    private val locationRequestListener: LocationRequestListener
) : BaseFragment(), LocationPermissionListener {

    private lateinit var binding: FragmentLocationBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentLocationBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        locationRequestListener.onRequestLocationPermission()
    }

    override fun onResume() {
        super.onResume()
        locationRequestListener.onCheckLocationPermission()
    }

    override fun onLocationPermissionChanged(granted: Boolean) {
        if (granted) {
            registerStepListener.onMoveToNextStep()
        } else {
            binding.llRegisterLocationLoadingWrapper.visibility = View.GONE
            binding.llRegisterLocationErrorWrapper.visibility = View.VISIBLE
        }
    }
}