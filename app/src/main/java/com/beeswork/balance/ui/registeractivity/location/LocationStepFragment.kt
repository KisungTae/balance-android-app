package com.beeswork.balance.ui.registeractivity.location

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.beeswork.balance.databinding.FragmentLocationBinding
import com.beeswork.balance.ui.common.BaseFragment
import com.beeswork.balance.ui.common.RegisterStepListener
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class LocationStepFragment(
    private val registerStepListener: RegisterStepListener
) : BaseFragment(), KodeinAware {

    override val kodein by closestKodein()
    private lateinit var binding: FragmentLocationBinding
    private lateinit var viewModel: LocationStepViewModel
    private val viewModelFactory: LocationStepViewModelFactory by instance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentLocationBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory).get(LocationStepViewModel::class.java)
        bind()
    }

    private fun bind() = lifecycleScope.launch {
        observeLocationGrantedLiveData()
    }

    private suspend fun observeLocationGrantedLiveData() {
        viewModel.locationGrantedLiveData.await().observe(viewLifecycleOwner) { granted ->
            if (granted == true) {
                registerStepListener.onMoveToNextStep()
            } else {
                binding.llRegisterLocationLoadingWrapper.visibility = View.GONE
                binding.llRegisterLocationErrorWrapper.visibility = View.VISIBLE
            }
        }
    }
}