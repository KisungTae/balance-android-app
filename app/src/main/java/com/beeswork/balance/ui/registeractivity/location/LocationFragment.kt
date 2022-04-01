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
import com.beeswork.balance.ui.registeractivity.RegisterActivity
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class LocationFragment : Fragment(), LocationPermissionListener {

    private lateinit var binding: FragmentLocationBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentLocationBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindUI()
        requestLocationPermission()
    }

    private fun bindUI() = lifecycleScope.launch {
        setupRegisterLocationRetryBtnListener()
    }

    private fun setupRegisterLocationRetryBtnListener() {
        binding.btnRegisterLocationRetry.setOnClickListener {
            binding.llRegisterLocationErrorWrapper.visibility = View.GONE
            binding.llRegisterLocationLoadingWrapper.visibility = View.VISIBLE
            binding.btnRegisterLocationRetry.visibility = View.INVISIBLE
            binding.btnRegisterLocationRetry.isEnabled = false
            requestLocationPermission()
        }
    }

    private fun requestLocationPermission() {
        activity?.let { _activity ->
            if (_activity is RegisterActivity) {
                _activity.requestLocationPermission()
            }
        }
    }

    override fun onLocationPermissionChanged(granted: Boolean) {
        if (granted) {
            activity?.let { _activity ->
                if (_activity is RegisterActivity) {
                    _activity.moveToNextTab()
                }
            }
        } else {
            binding.llRegisterLocationLoadingWrapper.visibility = View.GONE
            binding.llRegisterLocationErrorWrapper.visibility = View.VISIBLE
            binding.btnRegisterLocationRetry.visibility = View.VISIBLE
            binding.btnRegisterLocationRetry.isEnabled = true
        }
    }
}